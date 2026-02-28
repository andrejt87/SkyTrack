#!/usr/bin/env python3
"""Download OSM tiles z0-3 as base map for APK bundling."""
import os, sys, time, sqlite3, urllib.request, random, concurrent.futures, threading

OUTPUT = "/Users/andrej/.openclaw/workspace/SkyTrack/app/src/main/assets/skytrack-base.mbtiles"
MIN_ZOOM, MAX_ZOOM = 0, 3
TILE_SERVERS = [
    "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
]
USER_AGENT = "SkyTrack-OfflineMapBuilder/1.0 (andrej.tupikin@googlemail.com)"
lock = threading.Lock()
downloaded = 0

def tms_y(z, y): return (1 << z) - 1 - y

def download_tile(args):
    global downloaded
    z, x, y = args
    url = random.choice(TILE_SERVERS).format(z=z, x=x, y=y)
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = resp.read()
            with lock: downloaded += 1
            return (z, x, tms_y(z, y), data)
    except: return None

def main():
    total = sum(4**z for z in range(MIN_ZOOM, MAX_ZOOM + 1))
    print(f"Downloading {total} base tiles (z{MIN_ZOOM}-{MAX_ZOOM})")
    
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    if os.path.exists(OUTPUT): os.remove(OUTPUT)
    
    db = sqlite3.connect(OUTPUT)
    db.execute("PRAGMA journal_mode=DELETE")
    db.execute("PRAGMA synchronous=OFF")
    db.execute("CREATE TABLE metadata (name TEXT PRIMARY KEY, value TEXT)")
    db.execute("CREATE TABLE tiles (zoom_level INTEGER, tile_column INTEGER, tile_row INTEGER, tile_data BLOB, PRIMARY KEY (zoom_level, tile_column, tile_row))")
    for k, v in {"name":"SkyTrack Base","format":"png","minzoom":"0","maxzoom":"3","type":"baselayer","attribution":"© OpenStreetMap contributors","bounds":"-180,-85.05,180,85.05"}.items():
        db.execute("INSERT INTO metadata VALUES (?,?)", (k, v))
    db.commit()
    
    tiles = [(z, x, y) for z in range(MIN_ZOOM, MAX_ZOOM+1) for x in range(2**z) for y in range(2**z)]
    batch = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=5) as ex:
        for r in ex.map(download_tile, tiles):
            if r: batch.append(r)
    
    db.executemany("INSERT OR REPLACE INTO tiles VALUES (?,?,?,?)", batch)
    db.execute("CREATE INDEX idx_tiles ON tiles (zoom_level, tile_column, tile_row)")
    db.commit()
    db.close()
    
    size = os.path.getsize(OUTPUT) / 1024
    print(f"Done! {downloaded}/{total} tiles, {size:.0f} KB -> {OUTPUT}")

if __name__ == "__main__":
    main()

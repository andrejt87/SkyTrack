#!/usr/bin/env python3
"""
Download OSM tiles z0-7 worldwide and pack into MBTiles.
Uses concurrent downloads for speed.
"""

import os
import sys
import time
import sqlite3
import urllib.request
import random
import concurrent.futures
import threading

OUTPUT = "/Users/andrej/.openclaw/workspace/SkyTrack/tools/output/skytrack-world.mbtiles"
MIN_ZOOM = 0
MAX_ZOOM = 8
TILE_SERVERS = [
    "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
]
USER_AGENT = "SkyTrack-OfflineMapBuilder/1.0 (andrej.tupikin@googlemail.com)"
MAX_WORKERS = 10

lock = threading.Lock()
downloaded = 0
failed = 0

def create_mbtiles(path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    if os.path.exists(path):
        os.remove(path)
    # Remove WAL files too
    for ext in ['-shm', '-wal']:
        f = path + ext
        if os.path.exists(f):
            os.remove(f)
    
    db = sqlite3.connect(path)
    db.execute("PRAGMA journal_mode=DELETE")  # No WAL for simplicity
    db.execute("PRAGMA synchronous=OFF")
    db.execute("PRAGMA cache_size=10000")
    db.execute("""CREATE TABLE metadata (name TEXT PRIMARY KEY, value TEXT)""")
    db.execute("""CREATE TABLE tiles (zoom_level INTEGER, tile_column INTEGER, tile_row INTEGER, tile_data BLOB, PRIMARY KEY (zoom_level, tile_column, tile_row))""")
    
    metadata = {
        "name": "SkyTrack World",
        "format": "png",
        "minzoom": str(MIN_ZOOM),
        "maxzoom": str(MAX_ZOOM),
        "type": "baselayer",
        "description": "OpenStreetMap tiles z0-7 for SkyTrack offline maps",
        "attribution": "© OpenStreetMap contributors",
        "bounds": "-180,-85.05,180,85.05",
        "center": "0,0,3",
    }
    for k, v in metadata.items():
        db.execute("INSERT INTO metadata VALUES (?, ?)", (k, v))
    db.commit()
    return db

def tms_y(z, y):
    return (1 << z) - 1 - y

def download_tile(args):
    global downloaded, failed
    z, x, y = args
    url = random.choice(TILE_SERVERS).format(z=z, x=x, y=y)
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req, timeout=15) as resp:
                data = resp.read()
                with lock:
                    downloaded += 1
                return (z, x, tms_y(z, y), data)
        except Exception as e:
            if attempt == 2:
                with lock:
                    failed += 1
                return None
            time.sleep(0.5)
    return None

def main():
    total_tiles = sum(4**z for z in range(MIN_ZOOM, MAX_ZOOM + 1))
    print(f"Downloading {total_tiles} tiles (z{MIN_ZOOM}-{MAX_ZOOM}) with {MAX_WORKERS} workers")
    
    db = create_mbtiles(OUTPUT)
    start_time = time.time()
    
    # Generate all tile coordinates
    all_tiles = []
    for z in range(MIN_ZOOM, MAX_ZOOM + 1):
        n = 2 ** z
        for x in range(n):
            for y in range(n):
                all_tiles.append((z, x, y))
    
    batch = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        for i, result in enumerate(executor.map(download_tile, all_tiles)):
            if result:
                batch.append(result)
            
            if len(batch) >= 500:
                db.executemany("INSERT OR REPLACE INTO tiles VALUES (?, ?, ?, ?)", batch)
                db.commit()
                batch = []
            
            if (i + 1) % 1000 == 0:
                elapsed = time.time() - start_time
                rate = (i + 1) / elapsed
                pct = (i + 1) / total_tiles * 100
                print(f"  {i+1}/{total_tiles} ({pct:.1f}%) | {downloaded} ok, {failed} failed | {rate:.1f} tiles/s")
    
    if batch:
        db.executemany("INSERT OR REPLACE INTO tiles VALUES (?, ?, ?, ?)", batch)
        db.commit()
    
    print("Creating index...")
    db.execute("CREATE INDEX IF NOT EXISTS idx_tiles ON tiles (zoom_level, tile_column, tile_row)")
    db.commit()
    db.close()
    
    elapsed = time.time() - start_time
    file_size_mb = os.path.getsize(OUTPUT) / 1024 / 1024
    print(f"\nDone! {downloaded} tiles, {failed} failed")
    print(f"File: {OUTPUT} ({file_size_mb:.1f} MB)")
    print(f"Time: {elapsed/60:.1f} minutes")

if __name__ == "__main__":
    main()

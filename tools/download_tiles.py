#!/usr/bin/env python3
"""
Download OSM tiles z0-7 worldwide and pack into MBTiles.
Uses tile.openstreetmap.org with rate limiting.
"""

import os
import sys
import time
import sqlite3
import urllib.request
import math
import random

OUTPUT = "/Users/andrej/.openclaw/workspace/SkyTrack/tools/output/skytrack-world.mbtiles"
MIN_ZOOM = 0
MAX_ZOOM = 7
TILE_SERVERS = [
    "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
]
USER_AGENT = "SkyTrack-OfflineMapBuilder/1.0 (andrej.tupikin@googlemail.com)"
DELAY = 0.05  # 50ms between requests = ~20 req/s across 3 servers

def create_mbtiles(path):
    """Create MBTiles SQLite database with proper schema."""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    if os.path.exists(path):
        os.remove(path)
    
    db = sqlite3.connect(path)
    db.execute("PRAGMA journal_mode=WAL")
    db.execute("""
        CREATE TABLE metadata (
            name TEXT PRIMARY KEY,
            value TEXT
        )
    """)
    db.execute("""
        CREATE TABLE tiles (
            zoom_level INTEGER,
            tile_column INTEGER,
            tile_row INTEGER,
            tile_data BLOB,
            PRIMARY KEY (zoom_level, tile_column, tile_row)
        )
    """)
    
    # MBTiles metadata
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
    """Convert OSM y to TMS y (MBTiles uses TMS scheme)."""
    return (1 << z) - 1 - y

def download_tile(z, x, y):
    """Download a single tile, return PNG bytes or None."""
    url = random.choice(TILE_SERVERS).format(z=z, x=x, y=y)
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            return resp.read()
    except Exception as e:
        print(f"  FAIL {z}/{x}/{y}: {e}", file=sys.stderr)
        return None

def main():
    total_tiles = sum(4**z for z in range(MIN_ZOOM, MAX_ZOOM + 1))
    print(f"Downloading {total_tiles} tiles (z{MIN_ZOOM}-{MAX_ZOOM}) to {OUTPUT}")
    
    db = create_mbtiles(OUTPUT)
    
    downloaded = 0
    failed = 0
    start_time = time.time()
    batch_size = 500  # commit every N tiles
    
    for z in range(MIN_ZOOM, MAX_ZOOM + 1):
        num_tiles = 2 ** z
        level_total = num_tiles * num_tiles
        level_done = 0
        print(f"\nZoom {z}: {level_total} tiles")
        
        for x in range(num_tiles):
            for y in range(num_tiles):
                data = download_tile(z, x, y)
                if data:
                    # MBTiles uses TMS y-axis (flipped)
                    db.execute(
                        "INSERT OR REPLACE INTO tiles VALUES (?, ?, ?, ?)",
                        (z, x, tms_y(z, y), data)
                    )
                    downloaded += 1
                else:
                    failed += 1
                
                level_done += 1
                
                if downloaded % batch_size == 0:
                    db.commit()
                
                if (downloaded + failed) % 1000 == 0:
                    elapsed = time.time() - start_time
                    rate = (downloaded + failed) / elapsed if elapsed > 0 else 0
                    pct = (downloaded + failed) / total_tiles * 100
                    print(f"  Progress: {downloaded + failed}/{total_tiles} ({pct:.1f}%) | "
                          f"{downloaded} ok, {failed} failed | {rate:.1f} tiles/s")
                
                time.sleep(DELAY)
    
    db.commit()
    
    # Create index for faster lookups
    print("\nCreating index...")
    db.execute("CREATE INDEX IF NOT EXISTS idx_tiles ON tiles (zoom_level, tile_column, tile_row)")
    db.commit()
    db.close()
    
    elapsed = time.time() - start_time
    file_size_mb = os.path.getsize(OUTPUT) / 1024 / 1024
    print(f"\nDone! {downloaded} tiles downloaded, {failed} failed")
    print(f"File: {OUTPUT} ({file_size_mb:.1f} MB)")
    print(f"Time: {elapsed/60:.1f} minutes")

if __name__ == "__main__":
    main()

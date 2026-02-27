# Offline-Kartenpaket generieren — Anleitung

**Projekt:** SkyTrack – Offline Flight Tracking App  
**Ziel:** MBTiles-Datei (Weltkarte, 400–500 MB) für vollständig offlinefähige Karten  
**Dateiname:** `skytrack-world.mbtiles`

---

## Übersicht

Die App benötigt ein vorgerendertes Kartenpaket im MBTiles-Format (SQLite-Datenbank mit Raster-Tiles). Dieses Paket wird einmalig erstellt und in die App eingebettet oder separat auf das Gerät kopiert.

### Zoom-Level-Strategie

| Zoom | Auflösung | Inhalt | Tiles (Welt) | Größe (ca.) |
|------|-----------|--------|-------------|-------------|
| 0 | 1 Tile | Ganzer Planet | 1 | < 1 KB |
| 1 | 4 Tiles | Hemisphären | 4 | < 10 KB |
| 2 | 16 Tiles | Kontinente | 16 | < 50 KB |
| 3 | 64 Tiles | Große Regionen | 64 | ~200 KB |
| 4 | 256 Tiles | Ländergruppen | 256 | ~1 MB |
| 5 | 1.024 Tiles | Einzelne Länder | 1.024 | ~4 MB |
| 6 | 4.096 Tiles | Regionen, Küstenlinien | 4.096 | ~15 MB |
| 7 | 16.384 Tiles | Großstädte sichtbar | 16.384 | ~60 MB |
| 8 | 65.536 Tiles | Stadtgebiete, Straßen | 65.536 | ~200 MB |

**Empfehlung:** Zoom 0–7 weltweit (~80 MB) + Zoom 8 weltweit (~200 MB) = **~280 MB**  
Optional: Zoom 9–10 nur für Flughafenbereiche (+100–200 MB)

---

## Methode 1: QGIS (Empfohlen)

### Voraussetzungen
- [QGIS](https://qgis.org/download/) (Version 3.x, kostenlos)
- Internetverbindung zum Herunterladen der Tiles
- 2–4 Stunden Rechenzeit

### Schritt-für-Schritt

#### 1. QGIS installieren und öffnen
```
https://qgis.org/download/
```

#### 2. OpenStreetMap als Tile-Layer hinzufügen
1. Im Browser-Panel (links) → **XYZ Tiles** → Rechtsklick → **Neue Verbindung**
2. Name: `OpenStreetMap`
3. URL: `https://tile.openstreetmap.org/{z}/{x}/{y}.png`
4. Min Zoom: 0, Max Zoom: 19
5. **OK** → Doppelklick auf den neuen Layer

#### 3. MBTiles generieren
1. Menü: **Sketching** → **Sketching toolbox** (oder **Processing** → **Sketching**)
2. Im Processing Toolbox: **Raster Tools** → **Generate XYZ tiles (MBTiles)**
3. Parameter:
   - **Extent:** Ganze Welt (-180, -85, 180, 85) — oder „Use Map Canvas Extent"
   - **Minimum zoom:** `0`
   - **Maximum zoom:** `7` (für ~80 MB) oder `8` (für ~280 MB)
   - **DPI:** `96` (Standard)
   - **Tile format:** `PNG`
   - **Quality:** 75 (für JPEG) — bei PNG irrelevant
   - **Output file:** `skytrack-world.mbtiles`
4. **Run** klicken

> **Achtung:** Zoom 8 weltweit = 65.536 Tiles. Das dauert 1–3 Stunden und benötigt ~200 MB.
> Zoom 9 weltweit = 262.144 Tiles → mehrere GB, **nicht empfohlen** für die gesamte Welt.

#### 4. Alternative: Natural Earth Stil verwenden (schöner für Flug-Tracking)

Anstatt OpenStreetMap-Tiles können Natural Earth-Vektordaten genutzt werden, die für Weltkarten-Ansichten besser geeignet sind:

1. Natural Earth Daten herunterladen:
   ```
   https://www.naturalearthdata.com/downloads/
   ```
   - 1:10m Cultural Vectors (Ländergrenzen, Städte)
   - 1:10m Physical Vectors (Küstenlinien, Seen, Flüsse)
   - 1:10m Raster (Cross Blended Hypso Raster)

2. In QGIS laden und stylen (dunkler Hintergrund für Dark Mode)

3. Dann **Generate XYZ tiles (MBTiles)** aus dem gestylten Projekt

---

## Methode 2: MOBAC (Mobile Atlas Creator)

### Voraussetzungen
- [MOBAC](https://mobac.sourceforge.io/) (Java-basiert, kostenlos)
- Java 8+ installiert

### Schritt-für-Schritt

#### 1. MOBAC herunterladen
```
https://mobac.sourceforge.io/
```

#### 2. Atlas erstellen
1. MOBAC starten
2. **Map Source:** OpenStreetMap Mapnik
3. **Atlas format:** `MBTiles SQLite`
4. **Zoom Levels:** 0, 1, 2, 3, 4, 5, 6, 7, 8 auswählen
5. **Selection:** Ganze Welt markieren (Rechteck über die gesamte Karte ziehen)
6. **Add Selection** klicken
7. **Create Atlas** klicken
8. Warten (kann mehrere Stunden dauern)

#### 3. Ergebnis
Die Datei `atlas.mbtiles` wird im MOBAC-Ausgabeordner erstellt.
Umbenennen in `skytrack-world.mbtiles`.

---

## Methode 3: Kommandozeile mit tippecanoe oder gdal

### Für Natural Earth Raster-Daten:

```bash
# Natural Earth Raster herunterladen (Cross Blended Hypso mit Relief)
wget https://naciscdn.org/naturalearth/10m/raster/NE1_HR_LC_SR_W_DR.zip
unzip NE1_HR_LC_SR_W_DR.zip

# In MBTiles konvertieren mit GDAL
gdal_translate -of MBTILES \
  -co "TILE_FORMAT=PNG" \
  -co "QUALITY=90" \
  -co "ZOOM_LEVEL_STRATEGY=UPPER" \
  NE1_HR_LC_SR_W_DR.tif \
  skytrack-world-base.mbtiles

# Zoom-Levels generieren
gdaladdo -r average skytrack-world-base.mbtiles 2 4 8 16 32 64 128
```

### Für OpenStreetMap-Daten (genauer, aber größer):

```bash
# tilemaker verwenden (Vector → Raster nicht direkt, aber für PBF → MBTiles)
# Alternativ: OSM Tile Downloader Script

# Python-Script zum Herunterladen von OSM-Tiles in MBTiles:
pip install mercantile requests

python3 download_tiles.py \
  --source "https://tile.openstreetmap.org/{z}/{x}/{y}.png" \
  --min-zoom 0 \
  --max-zoom 7 \
  --output skytrack-world.mbtiles
```

---

## Integration in die App

### Option A: In APK einbetten (für Dateien < 150 MB)

```
SkyTrack/app/src/main/assets/skytrack-world.mbtiles
```

> **Achtung:** Die Android APK-Größe hat ein Limit von ~150 MB für den Play Store.
> Bei 300+ MB Kartendaten ist Option B besser.

### Option B: Sideloading / Erster Start (empfohlen für große Dateien)

1. Die MBTiles-Datei auf das Gerät kopieren:
   ```bash
   adb push skytrack-world.mbtiles /sdcard/osmdroid/skytrack-world.mbtiles
   ```

2. Oder in den App-internen Speicher:
   ```bash
   adb push skytrack-world.mbtiles /data/data/com.skytrack.app/files/mbtiles/skytrack-world.mbtiles
   ```

3. Die App findet die Datei automatisch beim Start (der `OfflineTileProvider` durchsucht beide Pfade).

### Option C: Download beim ersten Start

Eine zukünftige Version könnte die MBTiles-Datei von einem Server herunterladen:
- Beim ersten Start: „Kartendaten herunterladen? (~300 MB)"
- Download mit Progress-Bar
- Einmalig, danach vollständig offline

---

## Code-Architektur

Die Offline-Karten werden durch den `OfflineTileProvider` verwaltet:

```
com.skytrack.app.data.map.OfflineTileProvider
```

### Suchreihenfolge für MBTiles:
1. `/data/data/com.skytrack.app/files/mbtiles/skytrack-world.mbtiles`
2. `/sdcard/osmdroid/*.mbtiles` (größte Datei wird gewählt)
3. APK Assets (`assets/skytrack-world.mbtiles`) → wird nach internal kopiert

### Fallback-Verhalten:
- **MBTiles gefunden:** Rein offline, kein Netzwerk-Request
- **MBTiles nicht gefunden:** Online-Tiles (MAPNIK) als Cache, funktioniert bei WLAN, blank im Flugmodus

### Zoom-Limitierung:
Der `OfflineTileProvider` liest `maxzoom` aus den MBTiles-Metadaten und setzt `mapView.maxZoomLevel` entsprechend.
Kein künstliches Hochskalieren über den verfügbaren Detailgrad hinaus.

---

## Qualitätsprüfung

Nach Erstellung der MBTiles-Datei:

```bash
# Dateigröße prüfen
ls -lh skytrack-world.mbtiles

# Metadaten auslesen
sqlite3 skytrack-world.mbtiles "SELECT * FROM metadata;"

# Tile-Anzahl prüfen
sqlite3 skytrack-world.mbtiles "SELECT zoom_level, COUNT(*) FROM tiles GROUP BY zoom_level;"

# Stichproben-Tile exportieren
sqlite3 skytrack-world.mbtiles "SELECT writefile('test-tile.png', tile_data) FROM tiles WHERE zoom_level=5 AND tile_column=16 AND tile_row=16;"
```

### Erwartete Ergebnisse:

| Prüfpunkt | Erwartung |
|-----------|-----------|
| Dateigröße | 200–500 MB |
| Zoom 0–7 vorhanden | Ja |
| Zoom 8 vorhanden (optional) | Ja, wenn gewünscht |
| Tile-Format | PNG 256×256 |
| Metadata `name` | "SkyTrack World Map" |
| Metadata `format` | "png" |
| Metadata `minzoom` | "0" |
| Metadata `maxzoom` | "7" oder "8" |

---

## Zeitaufwand

| Methode | Vorbereitung | Generierung (Zoom 0–7) | Generierung (Zoom 0–8) |
|---------|-------------|----------------------|----------------------|
| QGIS | 15 Min | 30–60 Min | 2–4 Std |
| MOBAC | 10 Min | 45–90 Min | 3–5 Std |
| GDAL/CLI | 20 Min | 20–40 Min | 1–3 Std |

---

## Empfehlung für SkyTrack v1.0

**Zoom 0–7 weltweit** (~80 MB) als MVP. Die Weltkarte zeigt auf diesen Zoom-Stufen:
- Kontinente und Ozeane klar erkennbar
- Ländergrenzen sichtbar
- Küstenlinien detailliert genug
- Großstädte als Punkte sichtbar
- Für einen Flug von FRA → JFK ist der Detailgrad voll ausreichend

Für v1.1: Zoom 8 weltweit hinzufügen (~280 MB gesamt), damit Flughafenbereiche besser sichtbar sind.

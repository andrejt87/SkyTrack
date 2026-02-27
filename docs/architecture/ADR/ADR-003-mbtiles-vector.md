# ADR-003: MBTiles Vektorkacheln (statt Raster)

## Status
Accepted

## Datum
2026-02-27

## Kontext

Offline-Karten müssen lokal auf dem Gerät gespeichert werden. Zwei Formate standen zur Auswahl: Raster-Tiles (PNG/JPEG) und Vektor-Tiles (PBF/MVT) im MBTiles-Container.

## Entscheidung

**MBTiles mit Vektorkacheln (PBF/Mapbox Vector Tiles)** als Offline-Kartenformat.

## Begründung

| Kriterium | Raster Tiles | Vektor Tiles |
|-----------|-------------|-------------|
| Dateigröße (Welt) | ~50–300 GB | ~8–20 GB |
| Skalierung | Pixelig bei Zoom | Vektoren → pixelperFekt |
| Styling zur Laufzeit | Nicht möglich | Vollständig anpassbar |
| Dark Mode | Separate Tiles nötig | Ein Tileset, N Styles |
| Datendichte | Nur Bild | Geometrie + Attribute |
| MapLibre Support | ✓ | ✓ (Primärformat) |
| Geeignet für Routen | Schlecht | Gut (Geometrieverarbeitung) |

## Konsequenzen

- **Positiv:** Drastisch reduzierte Offline-Datei-Größe
- **Positiv:** Dark Mode und Custom Styling ohne mehrere Tilesets
- **Positiv:** Attribute (Land, Region) direkt im Tile für Overfly-Feature
- **Negativ:** Komplexere Rendering-Pipeline (MapLibre übernimmt das)

# ADR-005: Natural Earth + OpenMapTiles als Kartendatenquellen

## Status
Accepted

## Datum
2026-02-27

## Kontext

SkyTrack benötigt Geodaten für: (a) Ländergrenzen / Regionen für Overfly-Feature, (b) Kartendarstellung (Küstenlinien, Gewässer, Administrativgrenzen), (c) Airport-Daten.

## Entscheidung

- **Natural Earth** für Ländergrenzen, Küstenlinien, Administrativgrenzen (CC0 Public Domain)
- **OpenMapTiles** Schema für Vektortile-Struktur und Kartendarstellung
- **OurAirports** für Airport-Datenbank (CC0)

## Begründung

| Quelle | Lizenz | Verwendung | Alternative |
|--------|--------|-----------|------------|
| Natural Earth | CC0 | Grenzen, Overfly | GADM (NC-Lizenz) |
| OpenMapTiles | ODbl | Kartendarstellung | Mapbox (proprietär) |
| OurAirports | CC0 | Airport-DB | ICAO (kostenpflichtig) |

### Natural Earth Vorteile
- Public Domain — keine Lizenzbeschränkungen
- Verschiedene Detailstufen (1:10m, 1:50m, 1:110m)
- Stabile, gepflegte Datenbasis für Ländergrenzen
- Direkte Integration mit GeoJSON / MBTiles-Toolchain

## Konsequenzen

- **Positiv:** Vollständig lizenzfreie Offline-Datenbasis
- **Positiv:** Gut dokumentiertes OpenMapTiles-Schema für MapLibre-Styles
- **Negativ:** Natural Earth nicht tagesaktuell (Grenzen ändern sich selten)

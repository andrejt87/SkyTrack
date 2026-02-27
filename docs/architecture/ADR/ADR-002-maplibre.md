# ADR-002: MapLibre GL Android (statt Mapbox)

## Status
Accepted

## Datum
2026-02-27

## Kontext

SkyTrack benötigt eine leistungsfähige Kartenkomponente mit Offline-Fähigkeit für Vektorkacheln. Mapbox war die ursprüngliche Implementierung, von der MapLibre 2021 abgezweigt wurde.

## Entscheidung

**MapLibre GL Android** (Version 11.x) statt Mapbox SDK.

## Begründung

| Kriterium | Mapbox | MapLibre |
|-----------|--------|----------|
| Lizenz | Proprietary (ab v10) | BSD-2-Clause (Open Source) |
| Kosten | Nutzungsgebühren ab Schwellwert | Kostenlos |
| API-Kompatibilität | Proprietäre API | Fork der freien Mapbox API |
| Offline MBTiles | Eingeschränkt | Vollständig unterstützt |
| Community | Mapbox-kontrolliert | Linux Foundation |
| Vendor Lock-in | Hoch | Keiner |
| Tile Source Freiheit | Mapbox-bevorzugt | Beliebige Quellen |

## Konsequenzen

- **Positiv:** Keine Lizenzkosten, kein API-Key-Pflicht für Offline-Betrieb
- **Positiv:** Vollständige MBTiles-Unterstützung für Offline-Vektorkarten
- **Positiv:** Freie Wahl der Tile-Quellen (OpenMapTiles, Natural Earth)
- **Negativ:** Etwas kleinere Community als Mapbox, aber wachsend

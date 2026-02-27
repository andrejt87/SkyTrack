# ADR-001: Kotlin + Jetpack Compose (statt Flutter)

## Status
Accepted

## Datum
2026-02-27

## Kontext

Für SkyTrack wurde eine plattformübergreifende Entwicklung (Flutter) als Option evaluiert. Das Projekt zielt jedoch ausschließlich auf Android ab und benötigt tiefen Zugriff auf Android-spezifische Sensor-APIs (Barometer, GNSS), Hintergrundservices und Android-Systemintegration.

## Entscheidung

Natives Android mit **Kotlin 2.0** und **Jetpack Compose** als UI-Framework.

## Begründung

| Kriterium | Flutter | Kotlin + Compose |
|-----------|---------|------------------|
| Android Sensor APIs | Plugins nötig (Lag) | Direktzugriff |
| Background Services | Eingeschränkt | Vollständig (ForegroundService) |
| Jetpack-Integration | Bridges nötig | Nativ |
| Performance (Rendering) | Eigene Engine | Hardware-Canvas, Skia/HWUI |
| Kotlin Coroutines | Eingeschränkt | First-class Support |
| Community/Tooling Android | Zweiklassig | Beste Android-Toolchain |
| Ziel-Plattformen | Multi | Android only → kein Vorteil |

## Konsequenzen

- **Positiv:** Voller Zugriff auf alle Android-APIs ohne Umwege
- **Positiv:** Kotlin Coroutines + Flow für reaktive Sensordaten
- **Positiv:** Hilt, Room, Compose — alles nativ ohne Bridges
- **Negativ:** Kein iOS-Port ohne Rewrite (bewusste Entscheidung)

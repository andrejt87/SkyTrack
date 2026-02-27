# ADR-006: Dark Mode als Standard-Theme

## Status
Accepted

## Datum
2026-02-27

## Kontext

SkyTrack wird primär in Flugzeugen benutzt — oft in abgedunkelten Kabinen, nachts oder mit Fensterverdunklern. Die App zeigt Karten, Tracking-Daten und Routen an.

## Entscheidung

**Dark Mode ist das Default-Theme** von SkyTrack. Light Mode ist als Option verfügbar.

## Begründung

### Nutzungskontext
- Flugzeugkabinen sind oft gedimmt (Nachtflüge, Schlafen der Mitreisenden)
- Karten sind in Dark Mode besser lesbar (Kontrast Routen/Beschriftungen)
- Dunkle Karten sind Standard in Aviation (Avionik-Displays, ForeFlight, etc.)

### Technische Vorteile
- OLED-Displays: Echter Schwarz-Pixel = 0 mW → Akkueinsparung
- Vektor-Tiles: Ein Tileset + Dark Style (kein separater Tile-Download)
- Blendung reduziert bei nächtlicher Nutzung

### Material Design 3 Support
```
SkyTrackTheme {
    darkTheme = true  // default
    dynamicColor = false  // eigene SkyTrack-Farben
}
```

## Konsequenzen

- **Positiv:** Optimale UX für primären Anwendungsfall (Flug in Kabine)
- **Positiv:** Akku-Schonend auf OLED-Displays
- **Positiv:** Konsistentes Look & Feel mit Aviation-Industrie-Standard
- **Negativ:** Light Mode muss explizit getestet werden (kein Default-Pfad)

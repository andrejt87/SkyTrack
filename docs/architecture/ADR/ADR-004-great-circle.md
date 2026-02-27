# ADR-004: Haversine-Formel für Großkreis-Fortschritt

## Status
Accepted

## Datum
2026-02-27

## Kontext

SkyTrack berechnet den Fortschritt eines Fluges entlang der Großkreisroute (kürzester Weg auf der Erdkugel). Verschiedene geodätische Berechnungsverfahren standen zur Auswahl.

## Entscheidung

**Haversine-Formel** für Distanz- und Fortschrittsberechnungen. Vincenty-Formel für hochpräzise Berechnungen wenn nötig.

## Begründung

| Kriterium | Euklidische Näherung | Haversine | Vincenty |
|-----------|---------------------|-----------|----------|
| Genauigkeit (kurze Dist.) | Gut | Sehr gut | Exzellent |
| Genauigkeit (interkont.) | Schlecht | Gut (< 0.3% Fehler) | Exzellent |
| Rechenaufwand | Minimal | Gering | Hoch |
| Polgebiete | Instabil | Stabil | Singulär an Polen |
| Implementierung | Trivial | Einfach | Komplex |

Für Flugdistanzen von 500–15.000 km ist Haversine mit < 0.3% Fehler mehr als ausreichend. Vincenty wird für spezielle Präzisionsanforderungen als Fallback implementiert.

## Konsequenzen

- **Positiv:** Einfache, stabile Implementierung in `:core:math`
- **Positiv:** Geringe CPU-Last für Echtzeit-Tracking (1 Hz Update-Rate)
- **Positiv:** Keine Singularität an den Polen
- **Negativ:** Marginaler Genauigkeitsverlust (~0.3%) gegenüber Vincenty — für Fluganzeige irrelevant

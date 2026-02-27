# SkyTrack — Module Catalog

All modules follow the naming convention `:category:name`.

---

## Application Module

### `:app`
- **Type:** Android Application
- **Namespace:** `com.skytrack.app`
- **Responsibility:** Application entry point, Activity, NavHost, Hilt setup, top-level dependency wiring
- **Depends on:** all `:feature:*`, `:core:design`, `:core:navigation`, `:domain`

---

## Domain Module

### `:domain`
- **Type:** Pure Kotlin (JVM library)
- **Responsibility:** Business logic, use cases, entity models, repository interfaces
- **Depends on:** nothing (zero framework dependencies)
- **Key contents:**
  - `UseCase` base classes
  - `Repository` interfaces (e.g. `FlightRepository`, `AirportRepository`)
  - Domain models (e.g. `FlightLeg`, `Waypoint`, `Airport`, `GeoPoint`)
  - Business rules (altitude calculation, timezone detection, overfly logic)

---

## Core Modules

### `:core:math`
- **Type:** Pure Kotlin (JVM library)
- **Responsibility:** Geodesic math utilities — Haversine distance, great-circle bearing, altitude conversions
- **Depends on:** nothing
- **Key contents:** `HaversineCalculator`, `GeoMath`, `AltitudeConverter`

### `:core:design`
- **Type:** Android library
- **Namespace:** `com.skytrack.core.design`
- **Responsibility:** Design system — theme, colors, typography, reusable Compose components
- **Depends on:** Compose BOM, Material3
- **Key contents:** `SkyTrackTheme`, `Colors`, `Typography`, shared UI components

### `:core:navigation`
- **Type:** Android library
- **Namespace:** `com.skytrack.core.navigation`
- **Responsibility:** Navigation graph definitions, route constants, NavController extensions
- **Depends on:** Navigation Compose

---

## Data Modules

### `:data:sensors`
- **Type:** Android library
- **Namespace:** `com.skytrack.data.sensors`
- **Responsibility:** GPS/GNSS data, barometric pressure, accelerometer — implements sensor repository interfaces
- **Depends on:** `:domain`, Play Services Location

### `:data:maps`
- **Type:** Android library
- **Namespace:** `com.skytrack.data.maps`
- **Responsibility:** Map tile management, MBTiles loading, MapLibre integration
- **Depends on:** `:domain`, `:core:math`, MapLibre GL

### `:data:airports`
- **Type:** Android library
- **Namespace:** `com.skytrack.data.airports`
- **Responsibility:** Airport database (ICAO/IATA), search, offline lookup
- **Depends on:** `:domain`, Room

### `:data:gazetteer`
- **Type:** Android library
- **Namespace:** `com.skytrack.data.gazetteer`
- **Responsibility:** Geographic name lookup, country/region data from Natural Earth
- **Depends on:** `:domain`, Room

### `:data:persistence`
- **Type:** Android library
- **Namespace:** `com.skytrack.data.persistence`
- **Responsibility:** Local persistence — Room database for flights/logbook, DataStore for settings
- **Depends on:** `:domain`, Room, DataStore

---

## Feature Modules

### `:feature:tracking`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.tracking`
- **Responsibility:** Real-time flight tracking UI, GPS state, altitude monitoring
- **Depends on:** `:domain`, `:core:design`, `:core:navigation`, `:data:sensors`

### `:feature:map`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.map`
- **Responsibility:** Map view screen with offline vector tiles, route overlay, aircraft position
- **Depends on:** `:domain`, `:core:design`, `:core:navigation`, `:data:maps`, MapLibre GL

### `:feature:dashboard`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.dashboard`
- **Responsibility:** Main dashboard / home screen with flight summary cards
- **Depends on:** `:domain`, `:core:design`, `:core:navigation`

### `:feature:setup`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.setup`
- **Responsibility:** Flight setup wizard — route input, departure/arrival airports
- **Depends on:** `:domain`, `:core:design`, `:core:navigation`, `:data:airports`

### `:feature:altitude`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.altitude`
- **Responsibility:** Altitude profile screen, terrain clearance display
- **Depends on:** `:domain`, `:core:design`, `:core:math`

### `:feature:timezone`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.timezone`
- **Responsibility:** Timezone crossing detection and display during flight
- **Depends on:** `:domain`, `:core:design`, `:data:gazetteer`

### `:feature:logbook`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.logbook`
- **Responsibility:** Flight logbook — history, statistics, export
- **Depends on:** `:domain`, `:core:design`, `:data:persistence`

### `:feature:overfly`
- **Type:** Android library
- **Namespace:** `com.skytrack.feature.overfly`
- **Responsibility:** Overflown countries/regions display, progress along great-circle route
- **Depends on:** `:domain`, `:core:design`, `:core:math`, `:data:gazetteer`

---

## Dependency Matrix

| Module | :domain | :core:math | :core:design | :core:navigation | :data:* |
|--------|---------|-----------|-------------|-----------------|--------|
| :app | ✓ | — | ✓ | ✓ | — |
| :feature:* | ✓ | selective | ✓ | ✓ | selective |
| :data:* | ✓ | selective | — | — | — |
| :core:design | — | — | — | — | — |
| :core:navigation | — | — | — | — | — |
| :core:math | — | — | — | — | — |
| :domain | — | — | — | — | — |

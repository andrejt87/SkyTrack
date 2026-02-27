# SkyTrack — System Architecture

## Overview

SkyTrack is a multi-module Android application built with Clean Architecture principles. The codebase is organized into layers with strict unidirectional dependency rules.

## Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│              UI Layer (:app, :feature:*) │
│   Jetpack Compose · ViewModel · Hilt     │
└─────────────────┬───────────────────────┘
                  │ depends on
┌─────────────────▼───────────────────────┐
│           Domain Layer (:domain)         │
│   UseCases · Entities · Repository IF   │
│         Pure Kotlin — No Android         │
└──────────┬──────────────────────────────┘
           │ implements
┌──────────▼──────────────────────────────┐
│         Data Layer (:data:*)             │
│  Room · DataStore · Sensors · MapLibre  │
└─────────────────────────────────────────┘
```

### Dependency Direction

```
UI → Domain ← Data
```

- **UI** depends on **Domain** (calls UseCases, observes flows)
- **Data** depends on **Domain** (implements repository interfaces)
- **Domain** has **zero** Android/framework dependencies

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|--------|
| Language | Kotlin | 2.0.21 |
| UI Toolkit | Jetpack Compose | BOM 2024.12.01 |
| DI | Hilt (Dagger) | 2.51.1 |
| Database | Room | 2.6.1 |
| Maps | MapLibre GL Android | 11.5.2 |
| Navigation | Navigation Compose | 2.8.5 |
| Async | Kotlin Coroutines + Flow | 1.8.1 |
| Build | Gradle | 8.7 |
| AGP | Android Gradle Plugin | 8.5.2 |
| KSP | Kotlin Symbol Processing | 2.0.21-1.0.27 |

## SDK Targets

| Property | Value |
|----------|-------|
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 29 (Android 10) |
| JVM Target | 17 |

## Architecture Pattern

**MVVM + Clean Architecture**

- `ViewModel` holds UI state as `StateFlow`
- `UseCase` classes encapsulate business logic
- Repository interfaces live in `:domain`, implementations in `:data:*`
- Hilt provides DI across all layers

## Module Graph (simplified)

```
:app
 ├── :feature:tracking
 ├── :feature:map
 ├── :feature:dashboard
 ├── :feature:setup
 ├── :feature:altitude
 ├── :feature:timezone
 ├── :feature:logbook
 ├── :feature:overfly
 ├── :core:design
 ├── :core:navigation
 └── :domain

:feature:* → :domain, :core:design, :core:navigation
:data:* → :domain
:domain → (nothing — pure Kotlin)
:core:math → (nothing — pure Kotlin)
```

## Key Design Decisions

See `docs/architecture/ADR/` for full Architecture Decision Records.

- Kotlin + Jetpack Compose (native Android, not Flutter)
- MapLibre GL (open source, offline-capable)
- MBTiles vector tiles (compact offline maps)
- Haversine formula for great-circle progress
- Natural Earth + OpenMapTiles data sources
- Dark mode as default theme

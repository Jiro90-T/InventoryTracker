# Inventory Tracker

A personal inventory manager for **home users and small businesses**. Track your items, snap a photo, scan a barcode, get reminded before warranties and consumables expire.

> Status: **M2 — personas, settings, and data export**. Users pick a mode on first launch (Home / Business / Collector), and the form adapts. Settings cover persona, currency, theme, and reminder lead-time. CSV export uses the system file picker.

---

## Features

| Feature | Status | Notes |
|---|---|---|
| Barcode scanning | **live** | CameraX preview + ML Kit decoder, de-duped per value |
| Item photos | **live** | Camera capture or gallery picker, stored in app-internal dir, Coil-rendered |
| Warranty tracking | **live** | Date picker in form; reminder scheduled N days before (configurable) |
| Expiry reminders | **live** | Date picker in form; reminder scheduled N days before (configurable) |
| Search & filtering | **live** | Room `LIKE` query + category filter chips with counts |
| Add/Edit/Delete | **live** | Full form with persona-conditional fields, photos, dates |
| Persona modes | **live** | Home / Business / Collector; changes suggested categories and form fields |
| Settings | **live** | Persona, currency, theme, reminder lead-time, CSV export |
| CSV export | **live** | Storage Access Framework → user picks destination |

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **MVVM** with **Hilt** DI
- **Room** for local persistence (offline-first)
- **CameraX** + **ML Kit** for barcode scanning
- **WorkManager** for scheduled reminders
- **Coil** for image loading
- Min SDK 24, target SDK 34

## Project structure

```
app/src/main/java/com/jiro/inventorytracker/
├── InventoryApp.kt            # Hilt @Application
├── MainActivity.kt            # Single-activity host
├── data/                      # Room: Item, ItemDao, AppDatabase
├── domain/                    # ItemRepository
├── di/                        # Hilt modules
├── export/                    # CSV exporter
├── media/                     # PhotoStorage (file storage + FileProvider)
├── persona/                   # Persona enum + UserPreferences (DataStore)
├── reminders/                 # WorkManager workers + scheduler
└── ui/
    ├── theme/                 # Material 3 theme (driven by UserPreferences)
    ├── onboarding/            # First-run persona chooser
    ├── home/                  # Inventory list + search + filter chips
    ├── scan/                  # Barcode camera screen + ML Kit analyzer
    ├── add/                   # Add/Edit Item form (persona-conditional)
    ├── detail/                # Item detail view
    └── settings/              # Persona, currency, theme, lead-time, CSV export
```

## Roadmap

- **M1** — ✅ Core flows (barcode, photos, full CRUD, reminders)
- **M2** — ✅ Personas, settings, CSV export, category filter chips
- **M3** — Cloud sync (Firebase or Supabase), PDF export, multi-user for small businesses
- **M4** — Receipt OCR, Wear OS companion, quick-add widget

## Building

This project uses Gradle. Open it in Android Studio (Hedgehog or newer) and sync, or:

```bash
./gradlew assembleDebug
```

The Gradle wrapper JAR is intentionally not committed; Android Studio will generate it on first sync, or run:

```bash
gradle wrapper --gradle-version 8.7
```

## License

TBD

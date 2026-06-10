# Inventory Tracker

A personal inventory manager for **home users and small businesses**. Track your items, snap a photo, scan a barcode, get reminded before warranties and consumables expire.

> Status: **M0 — project scaffold**. Features are stubbed and will be filled in over the next milestones.

---

## Features

| Feature | Status | Notes |
|---|---|---|
| Barcode scanning | scaffolded | CameraX + ML Kit wired in `ui/scan` |
| Item photos | planned | Camera/gallery + Coil for display |
| Warranty tracking | data model ready | `Item.warrantyExpiresAt` |
| Expiry reminders | scheduler stub | `ReminderScheduler` + `ReminderWorker` |
| Search & filtering | live | Room FTS-style `LIKE` query in `ItemDao.search` |

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
├── reminders/                 # WorkManager workers + scheduler
└── ui/
    ├── theme/                 # Material 3 theme
    ├── home/                  # Inventory list + search
    ├── scan/                  # Barcode camera screen
    └── detail/                # Item detail screen
```

## Roadmap

- **M1** — Wire ML Kit barcode decoder to `ScanScreen`; photo capture + gallery picker; full Add/Edit form
- **M2** — Reminder scheduling on save; settings screen; category chips
- **M3** — Export to CSV/PDF; backup/restore
- **M4** — Cloud sync (Firebase or Supabase) for small business multi-device use

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

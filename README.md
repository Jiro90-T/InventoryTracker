# Inventory Tracker

A personal inventory manager for **home users and small businesses**. Track your items, snap a photo, scan a barcode, get reminded before warranties and consumables expire.

> Status: **M1 — core flows live**. Barcode scanning, item CRUD with full fields, photo capture/gallery, and reminder scheduling all wired end-to-end.

---

## Features

| Feature | Status | Notes |
|---|---|---|
| Barcode scanning | **live** | CameraX preview + ML Kit decoder, de-duped per value |
| Item photos | **live** | Camera capture or gallery picker, stored in app-internal dir, Coil-rendered |
| Warranty tracking | **live** | Date picker in form; reminder scheduled 30 days before |
| Expiry reminders | **live** | Date picker in form; reminder scheduled 7 days before |
| Search & filtering | **live** | Room `LIKE` query, live as you type |
| Add/Edit/Delete | **live** | Full form with all fields, photos, dates |

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
├── media/                     # PhotoStorage (file storage + FileProvider)
├── reminders/                 # WorkManager workers + scheduler
└── ui/
    ├── theme/                 # Material 3 theme
    ├── home/                  # Inventory list + search
    ├── scan/                  # Barcode camera screen + ML Kit analyzer
    ├── add/                   # Add/Edit Item form
    └── detail/                # Item detail view
```

## Roadmap

- **M1** — ✅ Core flows (barcode, photos, full CRUD, reminders) — done
- **M2** — Category chips, settings screen (notification preferences, dark mode), item count badges
- **M3** — Export to CSV/PDF, backup/restore
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

package com.jiro.inventorytracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Item::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        const val DB_NAME = "inventory.db"

        /**
         * v1 -> v2: add persona-specific columns. All nullable, so a simple ALTER is safe.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE items ADD COLUMN purchase_currency TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN current_value REAL")
                db.execSQL("ALTER TABLE items ADD COLUMN condition TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN grade TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN era TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN assigned_to TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN asset_tag TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN manufacturer TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN model TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN serial_number TEXT")
            }
        }
    }
}

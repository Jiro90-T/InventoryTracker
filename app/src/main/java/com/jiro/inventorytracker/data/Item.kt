package com.jiro.inventorytracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "barcode") val barcode: String? = null,
    @ColumnInfo(name = "quantity") val quantity: Int = 1,
    @ColumnInfo(name = "location") val location: String? = null,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long? = null,
    @ColumnInfo(name = "purchase_price") val purchasePrice: Double? = null,
    @ColumnInfo(name = "purchase_currency") val purchaseCurrency: String? = null,
    @ColumnInfo(name = "current_value") val currentValue: Double? = null,
    @ColumnInfo(name = "warranty_expires_at") val warrantyExpiresAt: Long? = null,
    @ColumnInfo(name = "expiry_date") val expiryDate: Long? = null,
    @ColumnInfo(name = "photo_paths") val photoPaths: List<String> = emptyList(),
    @ColumnInfo(name = "notes") val notes: String? = null,

    // Persona-specific optional fields
    @ColumnInfo(name = "condition") val condition: String? = null,   // Condition enum name
    @ColumnInfo(name = "grade") val grade: String? = null,           // e.g. PSA 10, BGS 9.5
    @ColumnInfo(name = "era") val era: String? = null,               // e.g. "1980s", "Vintage"
    @ColumnInfo(name = "assigned_to") val assignedTo: String? = null, // business: assignee
    @ColumnInfo(name = "asset_tag") val assetTag: String? = null,     // business: inventory ID
    @ColumnInfo(name = "manufacturer") val manufacturer: String? = null,
    @ColumnInfo(name = "model") val model: String? = null,
    @ColumnInfo(name = "serial_number") val serialNumber: String? = null,

    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

package com.jiro.inventorytracker.export

import com.jiro.inventorytracker.domain.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    private val repository: ItemRepository
) {
    /**
     * Builds a CSV string from the current inventory. Suspends on IO.
     */
    suspend fun toCsv(): String = withContext(Dispatchers.IO) {
        val items = repository.observeAllOnce()
        buildString {
            // Header
            appendLine(
                listOf(
                    "name", "category", "barcode", "quantity", "location",
                    "purchase_date", "purchase_price", "purchase_currency",
                    "current_value", "warranty_expires_at", "expiry_date",
                    "manufacturer", "model", "serial_number",
                    "condition", "grade", "era",
                    "assigned_to", "asset_tag",
                    "notes", "created_at", "updated_at"
                ).joinToString(",") { csvField(it) }
            )
            val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            items.forEach { i ->
                appendLine(
                    listOf(
                        i.name,
                        i.category,
                        i.barcode,
                        i.quantity.toString(),
                        i.location,
                        i.purchaseDate?.let(fmt::format),
                        i.purchasePrice?.toString(),
                        i.purchaseCurrency,
                        i.currentValue?.toString(),
                        i.warrantyExpiresAt?.let(fmt::format),
                        i.expiryDate?.let(fmt::format),
                        i.manufacturer,
                        i.model,
                        i.serialNumber,
                        i.condition,
                        i.grade,
                        i.era,
                        i.assignedTo,
                        i.assetTag,
                        i.notes,
                        fmt.format(Date(i.createdAt)),
                        fmt.format(Date(i.updatedAt))
                    ).joinToString(",") { csvField(it) }
                )
            }
        }
    }

    private fun csvField(raw: String?): String {
        if (raw == null) return ""
        val needsQuotes = raw.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        val escaped = raw.replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}

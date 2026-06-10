package com.jiro.inventorytracker.ui.add

import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.persona.Condition

data class AddEditUiState(
    val name: String = "",
    val category: String = "",
    val barcode: String = "",
    val quantity: Int = 1,
    val location: String = "",
    val purchaseDate: Long? = null,
    val purchasePrice: Double? = null,
    val purchaseCurrency: String = "USD",
    val currentValue: Double? = null,
    val warrantyExpiresAt: Long? = null,
    val expiryDate: Long? = null,
    val photoPaths: List<String> = emptyList(),
    val notes: String = "",

    // Persona-specific
    val condition: Condition? = null,
    val grade: String = "",
    val era: String = "",
    val assignedTo: String = "",
    val assetTag: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val serialNumber: String = "",

    val error: String? = null
) {
    companion object {
        fun fromItem(item: Item): AddEditUiState = AddEditUiState(
            name = item.name,
            category = item.category.orEmpty(),
            barcode = item.barcode.orEmpty(),
            quantity = item.quantity,
            location = item.location.orEmpty(),
            purchaseDate = item.purchaseDate,
            purchasePrice = item.purchasePrice,
            purchaseCurrency = item.purchaseCurrency ?: "USD",
            currentValue = item.currentValue,
            warrantyExpiresAt = item.warrantyExpiresAt,
            expiryDate = item.expiryDate,
            photoPaths = item.photoPaths,
            notes = item.notes.orEmpty(),
            condition = Condition.fromName(item.condition),
            grade = item.grade.orEmpty(),
            era = item.era.orEmpty(),
            assignedTo = item.assignedTo.orEmpty(),
            assetTag = item.assetTag.orEmpty(),
            manufacturer = item.manufacturer.orEmpty(),
            model = item.model.orEmpty(),
            serialNumber = item.serialNumber.orEmpty()
        )
    }
}

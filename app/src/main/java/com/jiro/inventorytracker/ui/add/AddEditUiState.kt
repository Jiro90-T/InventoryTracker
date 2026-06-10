package com.jiro.inventorytracker.ui.add

import com.jiro.inventorytracker.data.Item

data class AddEditUiState(
    val name: String = "",
    val category: String = "",
    val barcode: String = "",
    val quantity: Int = 1,
    val location: String = "",
    val purchaseDate: Long? = null,
    val purchasePrice: Double? = null,
    val warrantyExpiresAt: Long? = null,
    val expiryDate: Long? = null,
    val photoPaths: List<String> = emptyList(),
    val notes: String = "",
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
            warrantyExpiresAt = item.warrantyExpiresAt,
            expiryDate = item.expiryDate,
            photoPaths = item.photoPaths,
            notes = item.notes.orEmpty()
        )
    }
}

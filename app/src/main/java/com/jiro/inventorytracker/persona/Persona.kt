package com.jiro.inventorytracker.persona

/**
 * Top-level user personas. The selected persona changes which extra fields the
 * Add/Edit form shows and which categories are suggested.
 */
enum class Persona(val displayName: String, val description: String) {
    HOME(
        displayName = "Home user",
        description = "Track household items, warranties, and what you own for insurance."
    ),
    BUSINESS(
        displayName = "Small business",
        description = "Manage office equipment, stock, assets, and maintenance schedules."
    ),
    COLLECTOR(
        displayName = "Collector",
        description = "Catalog trading cards, watches, sneakers, figures, antiques, and more."
    );

    companion object {
        fun fromName(name: String?): Persona? = entries.firstOrNull { it.name == name }
    }
}

/**
 * Suggested categories shown as quick-pick chips in the Add form. Empty entries
 * fall back to whatever the user has typed before.
 */
object PersonaCategories {
    val HOME = listOf(
        "Appliance", "Electronics", "Furniture", "Kitchen", "Tools",
        "Clothing", "Documents", "Jewelry", "Book", "Other"
    )
    val BUSINESS = listOf(
        "Equipment", "Furniture", "IT Hardware", "Vehicle", "Tool",
        "Consumable", "Stock", "Software License", "Other"
    )
    val COLLECTOR = listOf(
        "Trading Card", "Watch", "Sneaker", "Action Figure", "Antique",
        "Coin", "Stamp", "Comic", "Vinyl", "Art", "Other"
    )

    fun forPersona(persona: Persona): List<String> = when (persona) {
        Persona.HOME -> HOME
        Persona.BUSINESS -> BUSINESS
        Persona.COLLECTOR -> COLLECTOR
    }
}

/**
 * Conditions used for collector items and (optionally) business assets.
 */
enum class Condition(val displayName: String) {
    MINT("Mint"),
    NEAR_MINT("Near mint"),
    EXCELLENT("Excellent"),
    VERY_GOOD("Very good"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    FOR_PARTS("For parts");

    companion object {
        fun fromName(name: String?): Condition? = entries.firstOrNull { it.name == name }
    }
}

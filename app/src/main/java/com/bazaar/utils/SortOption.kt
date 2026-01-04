package com.bazaar.utils

enum class SortOption(val displayName: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
//    RECENTLY_ADDED("Recently Added"),
    STOCK_ALERTS("Low Stock"),
    PRICE_HIGH_TO_LOW("Price (High to Low)"),
    PRICE_LOW_TO_HIGH("Price (Low to High)")
}
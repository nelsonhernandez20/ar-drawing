package com.ardrawing.trace

data class CatalogImage(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val imageUrl: String,
    val isPremium: Boolean,
    val sortOrder: Int,
)

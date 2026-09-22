package com.ardrawing.gocho

enum class CatalogCategory(
    val key: String,
    val titleRes: Int,
) {
    DRAWINGS_3D("3d", R.string.category_3d),
    ANIMEMES("animemes", R.string.category_animemes),
    ANIMALS("animals", R.string.category_animals),
    CARS("cars", R.string.category_cars);

    companion object {
        val displayOrder: List<CatalogCategory> = entries.toList()
    }
}

data class LibraryCategoryRow(
    val category: CatalogCategory,
    val images: List<CatalogImage>,
)

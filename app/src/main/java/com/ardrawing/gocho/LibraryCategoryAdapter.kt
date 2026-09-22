package com.ardrawing.gocho

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.gocho.databinding.ItemLibraryCategoryBinding
import kotlinx.coroutines.CoroutineScope

class LibraryCategoryAdapter(
    private val scope: CoroutineScope,
    private val onImageClick: (CatalogImage) -> Unit,
) : RecyclerView.Adapter<LibraryCategoryAdapter.ViewHolder>() {

    private val rows = mutableListOf<LibraryCategoryRow>()

    fun submitRows(newRows: List<LibraryCategoryRow>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLibraryCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rows[position])
    }

    override fun getItemCount(): Int = rows.size

    inner class ViewHolder(
        private val binding: ItemLibraryCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val imagesAdapter = LibraryAdapter(
            scope = scope,
            onImageClick = onImageClick,
        )

        init {
            binding.recyclerCategoryImages.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false,
            )
            binding.recyclerCategoryImages.adapter = imagesAdapter
            binding.recyclerCategoryImages.setHasFixedSize(false)
            binding.recyclerCategoryImages.isNestedScrollingEnabled = true
        }

        fun bind(row: LibraryCategoryRow) {
            binding.textCategoryTitle.setText(row.category.titleRes)
            val empty = row.images.isEmpty()
            binding.textCategoryEmpty.isVisible = empty
            binding.recyclerCategoryImages.isVisible = !empty
            imagesAdapter.submitList(row.images)
        }
    }
}

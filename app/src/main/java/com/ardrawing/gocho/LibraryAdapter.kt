package com.ardrawing.gocho

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ardrawing.gocho.databinding.ItemLibraryImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class LibraryAdapter(
    private val scope: CoroutineScope,
    private val onImageClick: (CatalogImage) -> Unit,
) : ListAdapter<CatalogImage, LibraryAdapter.ViewHolder>(Diff) {

    private val client = OkHttpClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLibraryImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemLibraryImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var loadJob: Job? = null

        fun bind(item: CatalogImage) {
            binding.textTitle.text = item.title
            binding.iconLock.isVisible = item.isPremium

            loadJob?.cancel()
            binding.imageThumb.setImageDrawable(null)
            loadJob = scope.launch {
                val bitmap = loadThumbnail(item.thumbnailUrl)
                if (bitmap != null) {
                    binding.imageThumb.setImageBitmap(bitmap)
                }
            }

            binding.root.setOnClickListener { onImageClick(item) }
        }

        private suspend fun loadThumbnail(url: String) = withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val bytes = response.body?.bytes() ?: return@withContext null
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }.getOrNull()
        }
    }

    private object Diff : DiffUtil.ItemCallback<CatalogImage>() {
        override fun areItemsTheSame(old: CatalogImage, new: CatalogImage) = old.id == new.id
        override fun areContentsTheSame(old: CatalogImage, new: CatalogImage) = old == new
    }
}

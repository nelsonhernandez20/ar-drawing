package com.ardrawing.gocho

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ardrawing.gocho.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var catalogRepository: CatalogRepository
    private lateinit var adsHelper: AdsHelper

    private var mode: DrawingMode = DrawingMode.CAMERA
    private var allImages: List<CatalogImage> = emptyList()
    private var showPremiumTab: Boolean = false

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            binding.progressLoading.isVisible = true
            adsHelper.showInterstitial(this) {
                binding.progressLoading.isVisible = false
                startActivity(DrawingActivity.intent(this, mode, uri))
            }
        }
    }

    private lateinit var adapter: LibraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as ARDrawingApp
        catalogRepository = app.catalogRepository
        adsHelper = app.adsHelper

        mode = intent.getStringExtra(EXTRA_MODE)
            ?.let { runCatching { DrawingMode.valueOf(it) }.getOrNull() }
            ?: DrawingMode.CAMERA

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBar.updatePadding(top = bars.top)
            v.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }

        binding.toolbarLibrary.setNavigationOnClickListener { finish() }

        adapter = LibraryAdapter(
            scope = lifecycleScope,
            onImageClick = { image -> onCatalogImageSelected(image) },
        )
        binding.recyclerImages.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerImages.adapter = adapter

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_free))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_premium))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showPremiumTab = tab.position == 1
                refreshFilteredList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })

        binding.btnUseGallery.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        adsHelper.bindBanner(binding.adBanner)
        adsHelper.preloadInterstitial(this)
        loadCatalog()
    }

    private fun loadCatalog() {
        binding.progressLoading.isVisible = true
        binding.textEmpty.isVisible = false

        lifecycleScope.launch {
            val result = catalogRepository.fetchImages(this@LibraryActivity)
            binding.progressLoading.isVisible = false

            result.onSuccess { images ->
                allImages = images
                refreshFilteredList()
            }.onFailure { error ->
                binding.textEmpty.isVisible = true
                binding.textEmpty.text = getString(R.string.library_load_error, error.message ?: "")
            }
        }
    }

    private fun refreshFilteredList() {
        val filtered = allImages.filter { image ->
            if (showPremiumTab) image.isPremium else !image.isPremium
        }
        adapter.submitList(filtered)
        val showEmpty = filtered.isEmpty() && !binding.progressLoading.isVisible
        binding.textEmpty.isVisible = showEmpty
        if (showEmpty) {
            binding.textEmpty.text = if (allImages.isEmpty()) {
                getString(R.string.library_empty)
            } else {
                getString(R.string.library_tab_empty)
            }
        }
    }

    private fun onCatalogImageSelected(image: CatalogImage) {
        if (image.isPremium) {
            binding.progressLoading.isVisible = true
            adsHelper.showInterstitial(this) {
                binding.progressLoading.isVisible = false
                openCatalogImage(image)
            }
        } else {
            openCatalogImage(image)
        }
    }

    private fun openCatalogImage(image: CatalogImage) {
        binding.progressLoading.isVisible = true
        lifecycleScope.launch {
            val result = ImageDownloadHelper.downloadTemplate(this@LibraryActivity, image)
            binding.progressLoading.isVisible = false

            result.onSuccess { uri ->
                startActivity(DrawingActivity.intent(this@LibraryActivity, mode, uri))
            }.onFailure {
                Toast.makeText(
                    this@LibraryActivity,
                    getString(R.string.download_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    companion object {
        private const val EXTRA_MODE = "extra_mode"

        fun intent(context: Context, mode: DrawingMode): Intent =
            Intent(context, LibraryActivity::class.java).apply {
                putExtra(EXTRA_MODE, mode.name)
            }
    }
}

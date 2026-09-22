package com.ardrawing.gocho

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardrawing.gocho.databinding.ActivityLibraryBinding
import kotlinx.coroutines.launch

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var catalogRepository: CatalogRepository
    private lateinit var adsHelper: AdsHelper

    private var mode: DrawingMode = DrawingMode.CAMERA

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

    private lateinit var categoryAdapter: LibraryCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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

        categoryAdapter = LibraryCategoryAdapter(
            scope = lifecycleScope,
            onImageClick = { image -> onCatalogImageSelected(image) },
        )
        binding.recyclerImages.layoutManager = LinearLayoutManager(this)
        binding.recyclerImages.adapter = categoryAdapter

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
                categoryAdapter.submitRows(groupByCategory(images))
                binding.textEmpty.isVisible = false
            }.onFailure { error ->
                categoryAdapter.submitRows(emptyList())
                binding.textEmpty.isVisible = true
                binding.textEmpty.text = getString(R.string.library_load_error, error.message ?: "")
            }
        }
    }

    private fun groupByCategory(images: List<CatalogImage>): List<LibraryCategoryRow> {
        val grouped = images.groupBy { it.category }
        return CatalogCategory.displayOrder.map { category ->
            LibraryCategoryRow(
                category = category,
                images = interleaveFreeAndPremium(grouped[category.key].orEmpty()),
            )
        }
    }

    private fun interleaveFreeAndPremium(images: List<CatalogImage>): List<CatalogImage> {
        val free = images.filter { !it.isPremium }
        val premium = images.filter { it.isPremium }
        val mixed = ArrayList<CatalogImage>(images.size)
        val limit = maxOf(free.size, premium.size)
        for (index in 0 until limit) {
            if (index < free.size) mixed.add(free[index])
            if (index < premium.size) mixed.add(premium[index])
        }
        return mixed
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

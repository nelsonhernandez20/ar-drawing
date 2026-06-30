package com.ardrawing.trace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.content.IntentCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.ardrawing.trace.databinding.ActivityDrawingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding

    private var mode: DrawingMode = DrawingMode.CAMERA
    private var imageUri: Uri? = null

    private var locked: Boolean = false
    private var torchOn: Boolean = false
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var overlayScale: Float = 1f
    /** Última posición del dedo en pantalla (raw); evita jitter al arrastrar la vista. */
    private var lastRawX: Float = 0f
    private var lastRawY: Float = 0f

    private enum class PanelTool { OPACITY, SCALE }

    private var activeTool: PanelTool = PanelTool.OPACITY
    private var lastPanelNavId: Int = R.id.menu_opacity
    private var sliderProgrammatic: Boolean = false
    private var toolsPanelExpanded: Boolean = true

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else Toast.makeText(this, R.string.camera_permission_rationale, Toast.LENGTH_LONG).show()
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (locked) return false
            val newScale = (overlayScale * detector.scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)
            overlayScale = newScale
            applyOverlayTransform()
            syncScaleSlider()
            return true
        }
    }

    private lateinit var scaleDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }

        mode = intent.getStringExtra(EXTRA_MODE)
            ?.let { runCatching { DrawingMode.valueOf(it) }.getOrNull() }
            ?: DrawingMode.CAMERA
        imageUri = IntentCompat.getParcelableExtra(intent, EXTRA_URI, Uri::class.java)

        if (imageUri == null) {
            finish()
            return
        }

        scaleDetector = ScaleGestureDetector(this, scaleListener)

        setupUiForMode()
        bindToolbar()
        bindOverlayGestures()
        bindSliders()
        bindBottomNav()
        bindFab()
        bindToolsPanelToggle()

        binding.overlayImage.viewTreeObserver.addOnGlobalLayoutListener(object :
            android.view.ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.overlayImage.pivotX = binding.overlayImage.width / 2f
                binding.overlayImage.pivotY = binding.overlayImage.height / 2f
                binding.overlayImage.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        loadImageFromGallery(imageUri!!)
    }

    private fun setupUiForMode() {
        when (mode) {
            DrawingMode.CAMERA -> {
                binding.previewView.isVisible = true
                binding.screenBackdrop.isVisible = false
                binding.bottomNav.menu.findItem(R.id.menu_camera).isVisible = true
                binding.bottomNav.menu.findItem(R.id.menu_flash).isVisible = hasFlash()
                ensureCameraPermissionAndStart()
            }

            DrawingMode.SCREEN -> {
                binding.previewView.isVisible = false
                binding.screenBackdrop.isVisible = true
                binding.bottomNav.menu.findItem(R.id.menu_camera).isVisible = false
                binding.bottomNav.menu.findItem(R.id.menu_flash).isVisible = false
            }
        }
        binding.bottomNav.selectedItemId = R.id.menu_opacity
        showTool(PanelTool.OPACITY)
    }

    private fun hasFlash(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private fun bindToolbar() {
        binding.toolbarDrawing.setNavigationOnClickListener { finish() }
        binding.toolbarDrawing.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_finish) {
                finish()
                true
            } else {
                false
            }
        }
    }

    private fun bindToolsPanelToggle() {
        binding.panelToggleBar.setOnClickListener {
            toolsPanelExpanded = !toolsPanelExpanded
            val transition = AutoTransition().apply { setDuration(280) }
            TransitionManager.beginDelayedTransition(binding.bottomCard, transition)
            binding.toolsPanelContent.isVisible = toolsPanelExpanded
            binding.panelChevron.rotation = if (toolsPanelExpanded) 0f else 180f
            binding.panelToggleBar.contentDescription = getString(
                if (toolsPanelExpanded) R.string.toggle_tools_collapse else R.string.toggle_tools_expand
            )
        }
    }

    private fun bindFab() {
        binding.fabLock.setOnClickListener {
            locked = !locked
            binding.fabLock.setImageResource(
                if (locked) R.drawable.ic_lock else R.drawable.ic_lock_open
            )
            binding.fabLock.contentDescription = getString(
                if (locked) R.string.unlock_image else R.string.lock_image
            )
        }
        binding.fabReset.setOnClickListener {
            overlayScale = 1f
            binding.overlayImage.translationX = 0f
            binding.overlayImage.translationY = 0f
            applyOverlayTransform()
            syncScaleSlider()
        }
    }

    private fun bindOverlayGestures() {
        binding.overlayImage.setOnTouchListener { v, event ->
            if (locked) return@setOnTouchListener false
            val handledScale = scaleDetector.onTouchEvent(event)
            if (!scaleDetector.isInProgress && event.pointerCount == 1) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        lastRawX = event.rawX
                        lastRawY = event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - lastRawX
                        val dy = event.rawY - lastRawY
                        lastRawX = event.rawX
                        lastRawY = event.rawY
                        binding.overlayImage.translationX += dx
                        binding.overlayImage.translationY += dy
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
            handledScale || event.pointerCount == 1
        }
    }

    private fun bindSliders() {
        binding.sliderMain.addOnChangeListener { _, value, fromUser ->
            if (!fromUser || sliderProgrammatic) return@addOnChangeListener
            when (activeTool) {
                PanelTool.OPACITY -> binding.overlayImage.alpha = value
                PanelTool.SCALE -> {
                    overlayScale = value
                    applyOverlayTransform()
                }
            }
        }
    }

    private fun bindBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_opacity -> {
                    lastPanelNavId = R.id.menu_opacity
                    showTool(PanelTool.OPACITY)
                    true
                }

                R.id.menu_scale -> {
                    lastPanelNavId = R.id.menu_scale
                    showTool(PanelTool.SCALE)
                    true
                }

                R.id.menu_camera -> {
                    if (mode == DrawingMode.CAMERA) flipCamera()
                    binding.bottomNav.selectedItemId = lastPanelNavId
                    true
                }

                R.id.menu_flash -> {
                    if (mode == DrawingMode.CAMERA) toggleTorch()
                    binding.bottomNav.selectedItemId = lastPanelNavId
                    true
                }

                else -> false
            }
        }
    }

    private fun showTool(tool: PanelTool) {
        activeTool = tool
        when (tool) {
            PanelTool.OPACITY -> {
                binding.toolTitle.setText(R.string.opacity)
                sliderProgrammatic = true
                binding.sliderMain.valueFrom = 0.1f
                binding.sliderMain.valueTo = 1f
                binding.sliderMain.stepSize = 0.01f
                binding.sliderMain.value =
                    min(1f, max(0.1f, binding.overlayImage.alpha))
                sliderProgrammatic = false
            }

            PanelTool.SCALE -> {
                binding.toolTitle.setText(R.string.scale)
                sliderProgrammatic = true
                binding.sliderMain.valueFrom = MIN_SCALE
                binding.sliderMain.valueTo = MAX_SCALE
                binding.sliderMain.stepSize = 0.01f
                binding.sliderMain.value =
                    min(MAX_SCALE, max(MIN_SCALE, overlayScale))
                sliderProgrammatic = false
            }
        }
    }

    private fun syncScaleSlider() {
        if (activeTool != PanelTool.SCALE) return
        sliderProgrammatic = true
        binding.sliderMain.value = overlayScale.coerceIn(MIN_SCALE, MAX_SCALE)
        sliderProgrammatic = false
    }

    private fun applyOverlayTransform() {
        binding.overlayImage.scaleX = overlayScale
        binding.overlayImage.scaleY = overlayScale
    }

    private fun loadImageFromGallery(uri: Uri) {
        lifecycleScope.launch {
            val bmp = withContext(Dispatchers.IO) {
                BitmapLoadHelper.loadDownsampled(this@DrawingActivity, uri, 2048)
            }
            if (bmp == null) {
                Toast.makeText(this@DrawingActivity, R.string.pick_image_required, Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding.overlayImage.setImageBitmap(bmp)
            binding.overlayImage.alpha = 0.65f
            overlayScale = 1f
            applyOverlayTransform()
            if (activeTool == PanelTool.OPACITY) {
                sliderProgrammatic = true
                binding.sliderMain.value = binding.overlayImage.alpha.coerceIn(0.1f, 1f)
                sliderProgrammatic = false
            }
        }
    }

    private fun ensureCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> startCamera()

            else -> permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (mode != DrawingMode.CAMERA) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return
        provider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = binding.previewView.surfaceProvider
        }

        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            camera = provider.bindToLifecycle(this, selector, preview)
            binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            camera?.cameraControl?.enableTorch(torchOn)
        } catch (_: Exception) {
            Toast.makeText(this, R.string.camera_permission_rationale, Toast.LENGTH_SHORT).show()
        }
    }

    private fun flipCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        torchOn = false
        bindCameraUseCases()
    }

    private fun toggleTorch() {
        if (!hasFlash()) return
        torchOn = !torchOn
        camera?.cameraControl?.enableTorch(torchOn)
    }

    companion object {
        private const val EXTRA_MODE = "extra_mode"
        private const val EXTRA_URI = "extra_uri"

        private const val MIN_SCALE = 0.35f
        private const val MAX_SCALE = 4f

        fun intent(context: Context, mode: DrawingMode, uri: Uri): Intent =
            Intent(context, DrawingActivity::class.java).apply {
                putExtra(EXTRA_MODE, mode.name)
                putExtra(EXTRA_URI, uri)
            }
    }
}

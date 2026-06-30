package com.ardrawing.trace

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.ardrawing.trace.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedCameraMode: Boolean = true
    private var launchInterstitialScheduled = false

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val mode = if (selectedCameraMode) DrawingMode.CAMERA else DrawingMode.SCREEN
            startActivity(DrawingActivity.intent(this, mode, uri))
        } else {
            Toast.makeText(this, R.string.pick_image_required, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(
                top = bars.top,
                left = bars.left,
                right = bars.right,
            )
            v.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }

        binding.cardCamera.setOnClickListener {
            selectedCameraMode = true
            refreshSelectionUi()
        }
        binding.cardScreen.setOnClickListener {
            selectedCameraMode = false
            refreshSelectionUi()
        }

        binding.btnContinue.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSupportAd.setOnClickListener {
            binding.btnSupportAd.isEnabled = false
            Toast.makeText(this, R.string.support_ad_loading, Toast.LENGTH_SHORT).show()
            StartIoAdsManager.showSupportVideo(
                activity = this,
                onUnavailable = {
                    binding.btnSupportAd.isEnabled = true
                    Toast.makeText(this, R.string.support_ad_unavailable, Toast.LENGTH_SHORT).show()
                },
                onCompleted = {
                    binding.btnSupportAd.isEnabled = true
                    Toast.makeText(this, R.string.support_ad_thanks, Toast.LENGTH_SHORT).show()
                },
                onShown = {
                    binding.btnSupportAd.isEnabled = true
                },
            )
        }

        binding.btnKofi.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL)))
        }

        refreshSelectionUi()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !launchInterstitialScheduled) {
            launchInterstitialScheduled = true
            StartIoAdsManager.scheduleLaunchInterstitial(this)
        }
    }

    private fun refreshSelectionUi() {
        val primary = MaterialColors.getColor(binding.cardCamera, com.google.android.material.R.attr.colorPrimary)

        if (selectedCameraMode) {
            binding.cardCamera.strokeColor = primary
            binding.cardCamera.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_selected)
            binding.iconCamera.setImageResource(R.drawable.ic_check_circle)

            binding.cardScreen.strokeColor = getColor(R.color.card_border_unselected)
            binding.cardScreen.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_normal)
            binding.iconScreen.setImageResource(R.drawable.ic_circle_outline)
        } else {
            binding.cardScreen.strokeColor = primary
            binding.cardScreen.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_selected)
            binding.iconScreen.setImageResource(R.drawable.ic_check_circle)

            binding.cardCamera.strokeColor = getColor(R.color.card_border_unselected)
            binding.cardCamera.strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_normal)
            binding.iconCamera.setImageResource(R.drawable.ic_circle_outline)
        }
    }

    companion object {
        private const val KOFI_URL = "https://ko-fi.com/gocholabs"
    }
}

package com.ardrawing.gocho

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.ardrawing.gocho.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedCameraMode: Boolean = true
    private var keepSplashVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(2_200L)
            keepSplashVisible = false
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = bars.top)
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
            val mode = if (selectedCameraMode) DrawingMode.CAMERA else DrawingMode.SCREEN
            startActivity(LibraryActivity.intent(this, mode))
        }

        val adsHelper = (application as ARDrawingApp).adsHelper
        adsHelper.bindBanner(binding.adBanner)

        refreshSelectionUi()
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
}

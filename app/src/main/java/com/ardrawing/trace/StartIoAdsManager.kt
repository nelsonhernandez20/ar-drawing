package com.ardrawing.trace

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.adlisteners.VideoListener
import com.startapp.sdk.adsbase.model.AdPreferences

object StartIoAdsManager {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var launchInterstitialShown = false
    private var launchInterstitialLoading = false
    private var launchInterstitial: StartAppAd? = null
    private var supportAd: StartAppAd? = null
    private var supportAdLoading = false
    private var pendingSupportCallbacks: SupportCallbacks? = null
    private var hostActivity: Activity? = null
    private var supportLoadStep = 0

    private val supportLoadTimeout = Runnable {
        if (supportAdLoading) {
            supportAdLoading = false
            supportAd = null
            pendingSupportCallbacks?.let { callbacks ->
                pendingSupportCallbacks = null
                callbacks.onUnavailable()
            }
        }
    }

    private data class SupportCallbacks(
        val onUnavailable: () -> Unit,
        val onCompleted: () -> Unit,
        val onShown: () -> Unit,
    )

    fun scheduleLaunchInterstitial(activity: Activity) {
        hostActivity = activity
        if (launchInterstitialShown || launchInterstitialLoading || activity.isFinishing) return

        mainHandler.postDelayed({
            if (!activity.isFinishing && !launchInterstitialShown) {
                loadAndShowLaunchInterstitial(activity)
            }
        }, LAUNCH_INTERSTITIAL_DELAY_MS)
    }

    fun showSupportVideo(
        activity: Activity,
        onUnavailable: () -> Unit,
        onCompleted: () -> Unit,
        onShown: () -> Unit,
    ) {
        hostActivity = activity
        cancelSupportTimeout()
        supportAdLoading = false
        supportAd = null
        supportLoadStep = 0
        pendingSupportCallbacks = SupportCallbacks(onUnavailable, onCompleted, onShown)
        loadSupportVideo(activity)
    }

    fun isSupportVideoReady(): Boolean = false

    private fun loadSupportVideo(activity: Activity) {
        if (activity.isFinishing || pendingSupportCallbacks == null) return

        supportAdLoading = true
        val mode = SUPPORT_LOAD_MODES.getOrElse(supportLoadStep) { return failSupportLoad() }
        val adTag = when (mode) {
            StartAppAd.AdMode.REWARDED_VIDEO -> TAG_SUPPORT_REWARDED
            StartAppAd.AdMode.VIDEO -> TAG_SUPPORT_VIDEO
            else -> TAG_SUPPORT_INTERSTITIAL_FALLBACK
        }
        val prefs = AdPreferences()
            .setAdTag(adTag)
            .also { it.setHardwareAccelerated(true) }

        supportAd = StartAppAd(activity).apply {
            loadAd(mode, prefs, object : AdEventListener {
                override fun onReceiveAd(ad: Ad) {
                    supportAdLoading = false
                    cancelSupportTimeout()
                    pendingSupportCallbacks?.let { displaySupportVideo(it) }
                }

                override fun onFailedToReceiveAd(ad: Ad?) {
                    supportLoadStep++
                    if (supportLoadStep < SUPPORT_LOAD_MODES.size) {
                        loadSupportVideo(activity)
                    } else {
                        failSupportLoad()
                    }
                }
            })
        }

        cancelSupportTimeout()
        mainHandler.postDelayed(supportLoadTimeout, SUPPORT_LOAD_TIMEOUT_MS)
    }

    private fun failSupportLoad() {
        cancelSupportTimeout()
        supportAdLoading = false
        supportAd = null
        pendingSupportCallbacks?.let { callbacks ->
            pendingSupportCallbacks = null
            callbacks.onUnavailable()
        }
    }

    private fun displaySupportVideo(callbacks: SupportCallbacks) {
        val ad = supportAd
        if (ad == null) {
            callbacks.onUnavailable()
            return
        }

        pendingSupportCallbacks = null
        var videoCompleted = false
        ad.setVideoListener(object : VideoListener {
            override fun onVideoCompleted() {
                videoCompleted = true
                callbacks.onCompleted()
            }
        })
        val shown = ad.showAd(object : AdDisplayListener {
            override fun adHidden(ad: Ad) {
                supportAd = null
                if (!videoCompleted) {
                    callbacks.onCompleted()
                }
            }

            override fun adDisplayed(ad: Ad) = Unit

            override fun adClicked(ad: Ad) = Unit

            override fun adNotDisplayed(ad: Ad) {
                supportAd = null
                callbacks.onUnavailable()
            }
        })
        if (!shown) {
            supportAd = null
            callbacks.onUnavailable()
            return
        }
        callbacks.onShown()
    }

    private fun cancelSupportTimeout() {
        mainHandler.removeCallbacks(supportLoadTimeout)
    }

    private fun loadAndShowLaunchInterstitial(activity: Activity) {
        if (launchInterstitialShown || launchInterstitialLoading || activity.isFinishing) return

        launchInterstitialLoading = true
        val prefs = AdPreferences()
            .setAdTag(TAG_LAUNCH_INTERSTITIAL)
            .also { it.setHardwareAccelerated(true) }
        launchInterstitial = StartAppAd(activity).apply {
            loadAd(prefs, object : AdEventListener {
                override fun onReceiveAd(ad: Ad) {
                    launchInterstitialLoading = false
                    if (launchInterstitialShown || activity.isFinishing) return
                    mainHandler.postDelayed({
                        if (launchInterstitialShown || activity.isFinishing) return@postDelayed
                        launchInterstitialShown = true
                        showAd(object : AdDisplayListener {
                            override fun adHidden(ad: Ad) {
                                launchInterstitial = null
                            }

                            override fun adDisplayed(ad: Ad) = Unit

                            override fun adClicked(ad: Ad) = Unit

                            override fun adNotDisplayed(ad: Ad) {
                                launchInterstitialShown = false
                                launchInterstitial = null
                            }
                        })
                    }, SHOW_AD_DELAY_MS)
                }

                override fun onFailedToReceiveAd(ad: Ad?) {
                    launchInterstitialLoading = false
                    launchInterstitial = null
                }
            })
        }
    }

    // Botón "Apoyar": 1) video recompensado → 2) video → 3) interstitial si no hay video
    private val SUPPORT_LOAD_MODES = arrayOf(
        StartAppAd.AdMode.REWARDED_VIDEO,
        StartAppAd.AdMode.VIDEO,
        StartAppAd.AdMode.AUTOMATIC,
    )

    private const val TAG_LAUNCH_INTERSTITIAL = "launch_interstitial"
    private const val TAG_SUPPORT_REWARDED = "support_rewarded"
    private const val TAG_SUPPORT_VIDEO = "support_video"
    private const val TAG_SUPPORT_INTERSTITIAL_FALLBACK = "support_interstitial_fallback"
    private const val LAUNCH_INTERSTITIAL_DELAY_MS = 2500L
    private const val SHOW_AD_DELAY_MS = 300L
    private const val SUPPORT_LOAD_TIMEOUT_MS = 15_000L
}

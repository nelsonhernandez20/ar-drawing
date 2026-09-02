package com.ardrawing.gocho

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.lang.ref.WeakReference

class AdsHelper(private val appContext: Context) {

    private var interstitial: InterstitialAd? = null
    private var loadingInterstitial = false
    private var pendingShow: ((InterstitialAd?) -> Unit)? = null
    private var lastActivity: WeakReference<Activity>? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val loadTimeout = Runnable {
        loadingInterstitial = false
        deliverPending(null)
    }

    fun initialize() {
        MobileAds.initialize(appContext) {}
    }

    fun bindBanner(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }

    fun preloadInterstitial(activity: Activity) {
        lastActivity = WeakReference(activity)
        if (loadingInterstitial || interstitial != null) return
        loadingInterstitial = true

        InterstitialAd.load(
            activity,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loadingInterstitial = false
                    interstitial = ad
                    deliverPending(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadingInterstitial = false
                    interstitial = null
                    deliverPending(null)
                }
            },
        )
    }

    fun showInterstitial(activity: Activity, onFinished: () -> Unit) {
        lastActivity = WeakReference(activity)
        val ready = interstitial
        if (ready != null) {
            present(activity, ready, onFinished)
            return
        }

        pendingShow = { ad ->
            if (!activity.isFinishing && !activity.isDestroyed && ad != null) {
                present(activity, ad, onFinished)
            } else {
                onFinished()
            }
        }
        preloadInterstitial(activity)
        mainHandler.removeCallbacks(loadTimeout)
        mainHandler.postDelayed(loadTimeout, LOAD_TIMEOUT_MS)
    }

    private fun deliverPending(ad: InterstitialAd?) {
        val callback = pendingShow ?: return
        pendingShow = null
        mainHandler.removeCallbacks(loadTimeout)
        callback(ad)
    }

    private fun present(activity: Activity, ad: InterstitialAd, onFinished: () -> Unit) {
        interstitial = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                lastActivity?.get()?.let { preloadInterstitial(it) }
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                lastActivity?.get()?.let { preloadInterstitial(it) }
                onFinished()
            }
        }
        ad.show(activity)
    }

    companion object {
        private const val LOAD_TIMEOUT_MS = 8_000L
    }
}

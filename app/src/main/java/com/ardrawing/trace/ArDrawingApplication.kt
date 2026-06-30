package com.ardrawing.trace

import android.app.Application
import com.startapp.sdk.adsbase.StartAppSDK

class ArDrawingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StartAppSDK.setTestAdsEnabled(true)
        }
    }

    companion object {
        const val START_IO_APP_ID = "205418622"
    }
}

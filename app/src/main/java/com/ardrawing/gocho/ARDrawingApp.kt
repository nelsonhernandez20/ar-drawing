package com.ardrawing.gocho

import android.app.Application

class ARDrawingApp : Application() {

    lateinit var catalogRepository: CatalogRepository
        private set

    lateinit var adsHelper: AdsHelper
        private set

    override fun onCreate() {
        super.onCreate()
        catalogRepository = CatalogRepository()
        adsHelper = AdsHelper(this)
        adsHelper.initialize()
    }
}

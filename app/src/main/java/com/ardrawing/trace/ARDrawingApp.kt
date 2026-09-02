package com.ardrawing.trace

import android.app.Application

class ARDrawingApp : Application() {

    lateinit var billingRepository: BillingRepository
        private set

    lateinit var catalogRepository: CatalogRepository
        private set

    override fun onCreate() {
        super.onCreate()
        billingRepository = BillingRepository(this)
        catalogRepository = CatalogRepository()
        billingRepository.connect()
    }
}

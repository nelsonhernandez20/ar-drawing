package com.ardrawing.trace

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingRepository(
    private val context: Context,
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _isPremiumActive = MutableStateFlow(false)
    val isPremiumActive: StateFlow<Boolean> = _isPremiumActive.asStateFlow()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    private val _billingReady = MutableStateFlow(false)
    val billingReady: StateFlow<Boolean> = _billingReady.asStateFlow()

    private var connectionAttempted = false

    fun connect() {
        if (billingClient.isReady) {
            _billingReady.value = true
            refreshPurchases()
            queryProductDetails()
            return
        }
        if (connectionAttempted) return
        connectionAttempted = true

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingReady.value = true
                    refreshPurchases()
                    queryProductDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingReady.value = false
                connectionAttempted = false
            }
        })
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) return

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                updatePremiumFromPurchases(purchases)
                purchases.forEach { acknowledgeIfNeeded(it) }
            }
        }
    }

    private fun queryProductDetails() {
        if (!billingClient.isReady) return

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(BillingConstants.PREMIUM_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                ),
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, detailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = detailsList.firstOrNull()
            }
        }
    }

    fun launchSubscriptionPurchase(activity: Activity): BillingResult? {
        val details = _productDetails.value
        if (details == null) {
            connect()
            queryProductDetails()
            return null
        }

        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) return null

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        return billingClient.launchBillingFlow(activity, flowParams)
    }

    fun formattedPrice(): String? {
        val details = _productDetails.value ?: return null
        return details.subscriptionOfferDetails?.firstOrNull()
            ?.pricingPhases?.pricingPhaseList?.firstOrNull()
            ?.formattedPrice
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            updatePremiumFromPurchases(purchases)
            purchases.forEach { acknowledgeIfNeeded(it) }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            refreshPurchases()
        }
    }

    private fun updatePremiumFromPurchases(purchases: List<Purchase>) {
        val active = purchases.any { purchase ->
            purchase.products.contains(BillingConstants.PREMIUM_SUBSCRIPTION_ID) &&
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        _isPremiumActive.value = active
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { _ -> }
    }
}

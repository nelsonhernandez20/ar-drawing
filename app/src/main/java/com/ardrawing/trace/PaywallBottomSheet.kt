package com.ardrawing.trace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ardrawing.trace.databinding.BottomSheetPaywallBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaywallBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = BottomSheetPaywallBinding.inflate(inflater, container, false)
        val app = requireActivity().application as ARDrawingApp
        val billing = app.billingRepository

        binding.textPrice.text = billing.formattedPrice() ?: getString(R.string.price_loading)

        binding.btnSubscribe.setOnClickListener {
            billing.launchSubscriptionPurchase(requireActivity())
        }

        return binding.root
    }
}

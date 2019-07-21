package ru.merkulyevsasha.coreandroid.common

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

object AdViewHelper {
    fun loadBannerAd(adView: AdView, debugMode: Boolean) {
        val adRequest = if (debugMode)
            AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        else
            AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        adView.loadAd(adRequest)
    }
}
package ru.merkulyevsasha.news.presentation.common

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import ru.merkulyevsasha.news.BuildConfig

object AdViewHelper {
    fun loadBannerAd(adView: AdView) {
        val adRequest = if (BuildConfig.DEBUG_MODE)
            AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        else
            AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        adView.loadAd(adRequest)
    }
}
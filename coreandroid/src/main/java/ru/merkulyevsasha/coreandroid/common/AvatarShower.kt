package ru.merkulyevsasha.coreandroid.common

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions

class AvatarShower {

    private val noCacheOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .onlyRetrieveFromCache(false)

    private val cacheOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(false)
        .onlyRetrieveFromCache(true)

    fun showWithoutCache(context: Context, emptyAvatarResId: Int, fileName: String, authorization: String, imageViewAvatar: ImageView) {
        val url = GlideUrl(fileName, LazyHeaders.Builder()
            .addHeader("Authorization", authorization)
            .build())
        Glide.with(imageViewAvatar).load(url).apply(noCacheOptions).into(imageViewAvatar)
            .onLoadFailed(ContextCompat.getDrawable(context, emptyAvatarResId))
    }

    fun showWithCache(context: Context, emptyAvatarResId: Int, fileName: String, authorization: String, imageViewAvatar: ImageView) {
        val url = GlideUrl(fileName, LazyHeaders.Builder()
            .addHeader("Authorization", authorization)
            .build())
        Glide.with(imageViewAvatar).load(url).apply(noCacheOptions).into(imageViewAvatar)
            .onLoadFailed(ContextCompat.getDrawable(context, emptyAvatarResId))
    }
}

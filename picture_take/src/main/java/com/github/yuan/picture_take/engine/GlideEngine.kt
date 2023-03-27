package com.github.yuan.picture_take.engine

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.utils.ActivityCompatHelper
import com.github.yuan.picture_take.utils.GlideApp


/**
 * Glide加载引擎
 * @author Yuang
 */
object GlideEngine : ImageEngine {

    object InstanceHolder {
        val instance = GlideEngine
    }

    fun createGlideEngine(): GlideEngine {
        return InstanceHolder.instance
    }

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
    }

    override fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ) {

        GlideApp.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        GlideApp.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)

    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        GlideApp.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        GlideApp.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        GlideApp.with(context).resumeRequests()
    }
}
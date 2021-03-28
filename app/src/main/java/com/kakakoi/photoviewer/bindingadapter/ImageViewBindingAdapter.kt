package com.kakakoi.photoviewer.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.glide.GlideApp
import java.io.File

object ImageViewBindingAdapter {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String) {
        GlideApp.with(view.context)
            .load(File("${view.context.filesDir}/$url"))
            .placeholder(R.drawable.ic_sharp_crop_square_24)
            .error(R.drawable.ic_sharp_crop_square_error_24)
            //.override(mImageWidth, mImageWidth)
            .into(view)
    }
}
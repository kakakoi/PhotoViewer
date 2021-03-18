package com.kakakoi.photoviewer.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.glide.GlideApp

class PhotoAdapter(private var mList: Array<String>, private var spanCount: Int) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.findViewById<View>(R.id.card_image_view) as ImageView
    }

    private var mImageWidth = 0
    private var mParent: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        mParent = parent
        mImageWidth = parent.width / spanCount
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.photo_adapter, parent, false)
        return PhotoViewHolder(item)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.sampleImg.setImageResource(R.mipmap.ic_launcher_round)

        GlideApp.with(mParent!!.context)
            .load(R.mipmap.ic_launcher)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .override(mImageWidth, mImageWidth)
            .into(holder.sampleImg)
    }

    override fun onViewRecycled(holder: PhotoViewHolder) {
        super.onViewRecycled(holder)
//        Glide.clear(viewHolder.getImageView());
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}
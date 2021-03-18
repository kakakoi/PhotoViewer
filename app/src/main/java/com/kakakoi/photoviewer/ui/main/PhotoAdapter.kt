package com.kakakoi.photoviewer.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.databinding.PhotoAdapterBinding
import com.kakakoi.photoviewer.glide.GlideApp

private object DiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}

class PhotoAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val viewModel: MainViewModel,
    //private val spanCount: Int
    ) : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(DiffCallback) {

    class PhotoViewHolder(private val binding: PhotoAdapterBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Photo, viewLifecycleOwner: LifecycleOwner, viewModel: MainViewModel){
            binding.run {
                lifecycleOwner = viewLifecycleOwner
                photo = item
                this.viewModel = viewModel

                executePendingBindings()
            }
        }
    }

    //private var mImageWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        //mImageWidth = parent.width / spanCount
        val layoutInflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(PhotoAdapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), viewLifecycleOwner, viewModel)
/*
        mParent?.let {
            GlideApp.with(it.context)
                .load(R.mipmap.ic_launcher)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .override(mImageWidth, mImageWidth)
                .into(holder.sampleImg)
        }

 */
    }

    /*
    override fun onViewRecycled(holder: PhotoViewHolder) {
        super.onViewRecycled(holder)
        mParent?.let {
            GlideApp.with(it.context)
                .clear(holder.sampleImg)
        }
    }
     */
}
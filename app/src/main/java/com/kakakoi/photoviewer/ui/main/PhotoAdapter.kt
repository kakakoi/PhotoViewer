package com.kakakoi.photoviewer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.databinding.PhotoAdapterBinding

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(PhotoAdapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), viewLifecycleOwner, viewModel)
    }
}
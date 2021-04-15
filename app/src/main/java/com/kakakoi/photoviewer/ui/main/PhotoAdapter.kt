package com.kakakoi.photoviewer.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.databinding.PhotoAdapterBinding
import com.kakakoi.photoviewer.databinding.PhotoSectionAdapterBinding
import com.kakakoi.photoviewer.extensions.TAG
import com.kakakoi.photoviewer.extensions.getYearMonth

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
) : PagingDataAdapter<Photo, PhotoAdapter.PhotoViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_DETAIL = 2
        private const val VIEW_TYPE_SUMMARY = 3
        private const val VIEW_TYPE_BORDER = 4
    }

    class PhotoViewHolder(private val binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Photo, viewLifecycleOwner: LifecycleOwner, viewModel: MainViewModel){
            when(binding) {
                is PhotoSectionAdapterBinding -> {
                    binding.run {
                        lifecycleOwner = viewLifecycleOwner
                        photo = item
                        this.viewModel = viewModel

                        executePendingBindings()
                    }
                }
                is PhotoAdapterBinding -> {
                    binding.run {
                        lifecycleOwner = viewLifecycleOwner
                        photo = item
                        this.viewModel = viewModel

                        executePendingBindings()
                    }
                }
                else -> throw IllegalArgumentException("Unknown bindingClass ${binding.javaClass.name}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            VIEW_TYPE_HEADER -> {
                PhotoViewHolder(PhotoSectionAdapterBinding.inflate(layoutInflater, parent, false))
            }
            VIEW_TYPE_DETAIL -> {
                PhotoViewHolder(PhotoAdapterBinding.inflate(layoutInflater, parent, false))
            }
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, viewLifecycleOwner, viewModel) }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < 1) return VIEW_TYPE_DETAIL

        val newItem = getItem(position)?.dateTimeOriginal ?: -1
        val oldItem = getItem(position - 1)?.dateTimeOriginal ?: -1
        if (newItem <= 0L || oldItem <= 0L) return VIEW_TYPE_DETAIL

        if (newItem.getYearMonth() == oldItem.getYearMonth())  return VIEW_TYPE_DETAIL

        Log.d(TAG, "getItemViewType: section is newTime=${newItem.getYearMonth()}/$newItem oldTime=${oldItem.getYearMonth()}/$oldItem")
        return VIEW_TYPE_HEADER
    }
}
package com.kakakoi.photoviewer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.data.Photo
import com.kakakoi.photoviewer.databinding.MemoriesAdapterBinding

private object MemoriesDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}

class MemoriesAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val viewModel: MemoriesViewModel,
) : ListAdapter<Photo, MemoriesAdapter.MemoriesViewHolder>(MemoriesDiffCallback) {

    companion object {
        const val VIEW_TYPE_HIGHLIGHT = 5
        const val SPAN_SIZE = 2
    }

    class MemoriesViewHolder(private val binding: MemoriesAdapterBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Photo, viewLifecycleOwner: LifecycleOwner, viewModel: MemoriesViewModel){
            binding.run {
                lifecycleOwner = viewLifecycleOwner
                photo = item
                this.viewModel = viewModel

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoriesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MemoriesViewHolder(MemoriesAdapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MemoriesViewHolder, position: Int) {
        holder.bind(getItem(position), viewLifecycleOwner, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_HIGHLIGHT
    }
}
package com.kakakoi.photoviewer.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.databinding.StorageAdapterBinding


private object DiffCallback : DiffUtil.ItemCallback<Storage>() {
    override fun areItemsTheSame(oldItem: Storage, newItem: Storage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Storage, newItem: Storage): Boolean {
        return oldItem == newItem
    }
}

class StorageAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val viewModel: MainSettingsViewModel,
    ) : ListAdapter<Storage, StorageAdapter.StorageViewHolder>(DiffCallback) {

        class StorageViewHolder(private val binding: StorageAdapterBinding):
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Storage, viewLifecycleOwner: LifecycleOwner, viewModel: MainSettingsViewModel){
                binding.run {
                    lifecycleOwner = viewLifecycleOwner
                    storage = item
                    this.viewModel = viewModel

                    executePendingBindings()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return StorageViewHolder(StorageAdapterBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: StorageViewHolder, position: Int) {
            holder.bind(getItem(position), viewLifecycleOwner, viewModel)
        }
    }
/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kakakoi.photoviewer.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.databinding.PhotosLoadStateFooterViewItemBinding

class PhotosLoadStateViewHolder(
    private val binding: PhotosLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.also {
            it.setOnClickListener { retry.invoke() }
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
        Log.d(TAG, "bind: #13")
    }

    companion object {
        const val TAG = "PhotosLoadStateViewHolder"
        fun create(parent: ViewGroup, retry: () -> Unit): PhotosLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photos_load_state_footer_view_item, parent, false)
            val binding = PhotosLoadStateFooterViewItemBinding.bind(view)
            return PhotosLoadStateViewHolder(binding, retry)
        }
    }
}

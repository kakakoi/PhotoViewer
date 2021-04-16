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
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PhotosLoadStateAdapter(
        private val retry: () -> Unit
) : LoadStateAdapter<PhotosLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PhotosLoadStateViewHolder, loadState: LoadState) {
        Log.d(TAG, "onBindViewHolder: #13 ${loadState.toString()}")
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PhotosLoadStateViewHolder {
        return PhotosLoadStateViewHolder.create(parent, retry)
    }

    companion object {
        const val TAG = "PhotosLoadStateAdapter"
    }
}

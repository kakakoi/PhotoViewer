package com.kakakoi.photoviewer.ui.main

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.databinding.MainFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val SPAN_COUNT = 4
        const val TAG = "MainFragment"
    }

    private val viewModel: MainViewModel by viewModels()
    private val memoriesViewModel: MemoriesViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var memoriesAdapter: MemoriesAdapter
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.enqueueWorks()
        viewModel.onTransit.observe(viewLifecycleOwner, EventObserver {
            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(it.id)
            findNavController().navigate(action)
        })
        memoriesViewModel.onTransit.observe(viewLifecycleOwner, EventObserver {
            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(it.id)
            findNavController().navigate(action)
        })
        return MainFragmentBinding.inflate(inflater, container, false)
            .apply {
                binding = this
                this.viewModel = this@MainFragment.viewModel
                lifecycleOwner = viewLifecycleOwner

                list.run {
                    layoutManager = GridLayoutManager(context,SPAN_COUNT, GridLayoutManager.VERTICAL, false).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return when (adapter?.getItemViewType(position)) {
                                    MemoriesAdapter.VIEW_TYPE_HIGHLIGHT -> 2 // 1列
                                    else -> 1  // 2列
                                }                            }
                        }
                    }

                    val pAdapter = PhotoAdapter(viewLifecycleOwner, this@MainFragment.viewModel).let {
                        photoAdapter = it
                        initAdapter()
                    }
                    val mAdapter = MemoriesAdapter(viewLifecycleOwner, this@MainFragment.memoriesViewModel).also {
                        memoriesAdapter = it
                    }

                    val config = ConcatAdapter.Config.Builder()
                        .setIsolateViewTypes(false)  // デフォルトはtrue
                        .build()

                    adapter = ConcatAdapter(config, mAdapter, pAdapter)
                }
            }
            .run {
                root
            }
    }

    private var mSnackbarSyncPhotoState: Snackbar? = null

    //同期進捗SnackBarを表示
    private fun showSyncStateSnackbar(view: View) {
        val sb = StringBuilder()
        sb.append(viewModel.countAll.value)
        sb.append(getString(R.string.photo_unit))
        sb.append(":")
        sb.append(viewModel.countLoad.value)
        sb.append(getString(R.string.photo_unit))
        sb.append(getString(R.string.load_comp))

        val statusStr = sb.toString()
        if (mSnackbarSyncPhotoState == null) {
            mSnackbarSyncPhotoState = Snackbar.make(
                view, statusStr, Snackbar.LENGTH_INDEFINITE
            )
        }
        val snack = mSnackbarSyncPhotoState!!
        val snackTextView = snack.getView()
            .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackTextView.text = statusStr
        snackTextView.ellipsize = TextUtils.TruncateAt.MIDDLE
        //snackTextView.setSingleLine();
        snack.setAction(R.string.close,
            View.OnClickListener { snack.dismiss() })
        snack.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startStreamPhotos()
        memoriesViewModel.run {
            photos.observe(viewLifecycleOwner, {
                memoriesAdapter.submitList(it)
            })
        }
        viewModel.run {
            countAll.observe(viewLifecycleOwner, {
                view?.let {
                    showSyncStateSnackbar(it)
                }
            })
            countLoad.observe(viewLifecycleOwner, {
                view?.let {
                    showSyncStateSnackbar(it)
                }
            })
        }
    }

    private var searchJob: Job? = null

    private fun startStreamPhotos() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.photos.collectLatest {
                photoAdapter.submitData(it)
            }
        }
    }

    private fun initAdapter(): ConcatAdapter {
        val adapter = photoAdapter.withLoadStateHeaderAndFooter(
            header = PhotosLoadStateAdapter { photoAdapter.retry() },
            footer = PhotosLoadStateAdapter { photoAdapter.retry() }
        )
        photoAdapter.addLoadStateListener { loadState ->
            //読み込みできなかった場合
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
        }
        return adapter
    }
}
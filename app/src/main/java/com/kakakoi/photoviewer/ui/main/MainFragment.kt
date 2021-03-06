package com.kakakoi.photoviewer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakakoi.photoviewer.databinding.MainFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val SPAN_COUNT = 4
    }

    private val viewModel: MainViewModel by viewModels()
    private val memoriesViewModel: MemoriesViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var memoriesAdapter: MemoriesAdapter

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
                this.viewModel = this@MainFragment.viewModel
                lifecycleOwner = viewLifecycleOwner

                list.run {
                    layoutManager = GridLayoutManager(context,SPAN_COUNT, GridLayoutManager.VERTICAL, false)
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter = PhotoAdapter(viewLifecycleOwner, this@MainFragment.viewModel).also {
                        photoAdapter = it
                    }
                }

                memories.run {
                    //layoutManager = GridLayoutManager(context,1, GridLayoutManager.HORIZONTAL, false)
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

                    adapter = MemoriesAdapter(viewLifecycleOwner, this@MainFragment.memoriesViewModel).also {
                        memoriesAdapter = it
                    }

                }
            }
            .run {
                root
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.run {
            photos.observe(viewLifecycleOwner, {
                photoAdapter.submitList(it)
            })
        }
        memoriesViewModel.run {
            photos.observe(viewLifecycleOwner, {
                memoriesAdapter.submitList(it)
            })
        }
    }
}
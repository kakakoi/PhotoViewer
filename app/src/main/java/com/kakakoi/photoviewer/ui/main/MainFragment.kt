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
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.databinding.MainFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val SPAN_COUNT = 4
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.onTransit.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_main_to_settings)
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
    }
}
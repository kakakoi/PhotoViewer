package com.kakakoi.photoviewer.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.databinding.MainSettingsFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver


class MainSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = MainSettingsFragment()
        const val TAG = "MainSettingsFragment"
    }

    private val viewModel: MainSettingsViewModel by viewModels()
    private lateinit var storageAdapter: StorageAdapter
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.onTransit.observe(viewLifecycleOwner, EventObserver {
            when(it) {
                "storageSettings" -> {
                    findNavController().navigate(R.id.action_main_settings_to_storage_settings)
                }
                "onTransit" -> {
                    findNavController().navigate(R.id.action_main_settings_to_storage_settings)
                }
                else -> {
                    findNavController().navigate(R.id.action_main_settings_to_storage_settings)
                }
            }
        })
        rootView =  MainSettingsFragmentBinding.inflate(inflater, container, false)
            .apply {
                this.viewModel = this@MainSettingsFragment.viewModel
                lifecycleOwner = viewLifecycleOwner

                nasList.run {
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter = StorageAdapter(viewLifecycleOwner, this@MainSettingsFragment.viewModel).also {
                        storageAdapter = it
                    }
                }
            }
            .run {
                root
            }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.run {
            allStorages.observe(viewLifecycleOwner, {
                storageAdapter.submitList(it)
                viewModel.indexAndload()
            })
        }
    }
}
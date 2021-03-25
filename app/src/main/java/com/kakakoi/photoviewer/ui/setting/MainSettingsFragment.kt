package com.kakakoi.photoviewer.ui.setting

import android.os.Bundle
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.data.SmbStatus
import com.kakakoi.photoviewer.databinding.MainSettingsFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver


class MainSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = MainSettingsFragment()
        const val TAG = "MainSettingsFragment"
    }

    private val viewModel: MainSettingsViewModel by viewModels()
    private lateinit var storageAdapter: StorageAdapter
    private var mSnackbarSyncPhotoState: Snackbar? = null
    private lateinit var rootView: View
    private var smbRunning = false //prevent double clicks

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
                if (!smbRunning) {
                    viewModel.createIndex()
                } else {
                    Log.d(TAG, "onActivityCreated: Stop Double Tap!")
                }
            })

            smbStatus.observe(viewLifecycleOwner, {
                smbRunning = it.running
                showSyncStateSnackbar(it)
            })
        }
    }

    //同期進捗SnackBarを表示
    private fun showSyncStateSnackbar(smbStatus: SmbStatus) {
        val sb = StringBuilder()
        sb.append(smbStatus.status)
        sb.append("/")
        sb.append(smbStatus.path)
        Formatter.formatShortFileSize(activity, smbStatus.size)
        sb.append(" \n")
        sb.append(Formatter.formatShortFileSize(activity, smbStatus.size))
        val statusStr = sb.toString()

        if (mSnackbarSyncPhotoState == null) {
            mSnackbarSyncPhotoState = Snackbar.make(rootView, statusStr, Snackbar.LENGTH_INDEFINITE
            )
        }
        val snackTextView = mSnackbarSyncPhotoState?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackTextView?.text = statusStr
        snackTextView?.ellipsize = TextUtils.TruncateAt.MIDDLE
        //snackTextView.setSingleLine();
        mSnackbarSyncPhotoState?.setAction(R.string.close,
            View.OnClickListener { mSnackbarSyncPhotoState?.dismiss() })
        mSnackbarSyncPhotoState?.show()
    }
}
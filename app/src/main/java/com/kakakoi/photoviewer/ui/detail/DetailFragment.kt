package com.kakakoi.photoviewer.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.databinding.DetailFragmentBinding
import com.kakakoi.photoviewer.lib.EventObserver

class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
        const val TAG = "DetailFragment"
    }

    private val viewModel: DetailViewModel by viewModels()
    val args: DetailFragmentArgs by navArgs()
    var startX = 0
    var startY = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val photoId = args.photoId
        viewModel.setPhotoId(photoId)
        viewModel.onTransit.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.MainFragment)
        })
        return DetailFragmentBinding.inflate(inflater, container, false)
            .apply {
                this.viewModel = this@DetailFragment.viewModel
                lifecycleOwner = viewLifecycleOwner

                //TODO スワイプ操作
                imageDetail.setOnTouchListener { v, event ->
                    val action: Int = event.action
                    when(action) {
                        MotionEvent.ACTION_MOVE -> {
                            Log.d(TAG, "Action was MOVE X${event.x} Y${event.y}")
                            true
                        }
                        else -> {
                            true
                        }
                    }

                }
            }
            .run {
                root
            }
    }
}
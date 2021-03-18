package com.kakakoi.photoviewer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakakoi.photoviewer.R


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        const val SPAN_COUNT = 4
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val list = Array<String>(10) {"テキスト$it"}
        val adapter = PhotoAdapter(list, SPAN_COUNT)
        val layoutManager = GridLayoutManager(activity,SPAN_COUNT, GridLayoutManager.VERTICAL, false);

        // アダプターとレイアウトマネージャーをセット
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
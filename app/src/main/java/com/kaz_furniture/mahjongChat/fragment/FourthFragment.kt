package com.kaz_furniture.mahjongChat.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.DMListAdapter
import com.kaz_furniture.mahjongChat.data.DM
import com.kaz_furniture.mahjongChat.databinding.FragmentFourthBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var binding: FragmentFourthBinding? = null
    private lateinit var adapter: DMListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val dMList = ArrayList<DM>()
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DMListAdapter(layoutInflater, dMList)
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        )
        val bindingData: FragmentFourthBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
        viewModel.loadDMUsers(dMList, adapter)
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        bindingData.swipeRefresh.setOnRefreshListener {
            binding?.swipeRefresh?.isRefreshing = true
            viewModel.loadDMUsers(dMList, adapter)
            binding?.swipeRefresh?.isRefreshing = false
        }
    }
}
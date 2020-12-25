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
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.databinding.FragmentFourthBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel
import timber.log.Timber

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var binding: FragmentFourthBinding? = null
    private lateinit var adapter: DMListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()
    private val dMUserNameList = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bindingData: FragmentFourthBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
        adapter = DMListAdapter(layoutInflater, dMUserNameList)
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
                )
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
//        viewModel.dMUserNameSet(dMUserNameList, adapter)
//        Timber.d("dMUserNameList = $dMUserNameList")
        bindingData.swipeRefresh.setOnRefreshListener {
            binding?.swipeRefresh?.isRefreshing = true
            viewModel.dMUserNameSet(dMUserNameList, adapter)
            binding?.swipeRefresh?.isRefreshing = false
        }
    }
}
package com.kaz_furniture.mahjongChat.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.DMDetailActivity
import com.kaz_furniture.mahjongChat.databinding.FragmentFourthBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var binding: FragmentFourthBinding? = null
    private lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDMRooms()
        val bindingData: FragmentFourthBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
                )
        binding?.fab?.setOnClickListener {

        }
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.getDMRooms()
            binding?.swipeRefresh?.isRefreshing = false
        }
        viewModel.dMRoomList.observe(viewLifecycleOwner, Observer {
            binding?.dMRoomView?.customAdapter?.refresh(it)
        })
        viewModel.selectedDMRoom.observe(viewLifecycleOwner, Observer {
            launchDMRoomDetailActivity()
        })
    }

    private fun launchDMRoomDetailActivity() {
        val intent = DMDetailActivity.newIntent(requireContext())
        startActivityForResult(intent, REQUEST_CODE_DM_DETAIL)
    }

    companion object {
        private const val REQUEST_CODE_DM_DETAIL = 4000
    }
}
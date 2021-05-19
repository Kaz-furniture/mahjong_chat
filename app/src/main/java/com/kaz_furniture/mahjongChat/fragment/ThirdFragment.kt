package com.kaz_furniture.mahjongChat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.databinding.FragmentThirdBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class ThirdFragment : Fragment(R.layout.fragment_third) {

    private var binding: FragmentThirdBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentThirdBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
        binding?.lifecycleOwner = this
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.fetchNotifications()
            binding?.swipeRefresh?.isRefreshing = false
        }
        viewModel.notificationsLiveData.observe(viewLifecycleOwner, Observer {
            binding?.notificationsView?.customAdapter?.refresh(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchNotifications()
    }
}
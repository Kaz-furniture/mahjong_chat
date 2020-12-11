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
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.databinding.FragmentFourthBinding
import com.kaz_furniture.mahjongChat.databinding.FragmentThirdBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var binding: FragmentFourthBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentFourthBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
    }
}
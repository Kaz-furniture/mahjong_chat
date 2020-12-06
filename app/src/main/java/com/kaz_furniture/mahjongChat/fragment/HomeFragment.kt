package com.kaz_furniture.mahjongChat.fragment


import androidx.fragment.app.Fragment
import com.kaz_furniture.mahjongChat.PostListAdapter
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null

    private lateinit var adapter: PostListAdapter
}
package com.kaz_furniture.mahjongChat.fragment


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.FragmentHomeBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: PostListAdapter
    private val postList = ArrayList<Post>()
    lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentHomeBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return
        adapter = PostListAdapter(layoutInflater, postList)
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        )
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        viewModel.loadPostList(postList, adapter)
        bindingData.swipeRefresh.setOnRefreshListener {
            binding?.swipeRefresh?.isRefreshing = true
            viewModel.loadPostList(postList, adapter)
            binding?.swipeRefresh?.isRefreshing = false
        }
        viewModel.updateData.observe(viewLifecycleOwner, Observer {
            binding?.swipeRefresh?.isRefreshing = true
            viewModel.loadPostList(postList, adapter)
            binding?.swipeRefresh?.isRefreshing = false
        })
    }


}
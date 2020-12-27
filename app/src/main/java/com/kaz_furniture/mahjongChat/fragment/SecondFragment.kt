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
import com.kaz_furniture.mahjongChat.activity.PostDetailActivity
import com.kaz_furniture.mahjongChat.activity.ProfileActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.FragmentSecondBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class SecondFragment : Fragment(R.layout.fragment_second), PostListAdapter.Callback {

    private var binding: FragmentSecondBinding? = null
    private lateinit var adapter: PostListAdapter
    private val postList = ArrayList<Post>()
    lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentSecondBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return
        adapter = PostListAdapter(layoutInflater, postList, this)
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        )
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        binding?.swipeRefresh?.isRefreshing = true
        viewModel.loadPostList(postList, adapter)
        binding?.swipeRefresh?.isRefreshing = false
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

    override fun openDetail(post: Post) {
        val intent = PostDetailActivity.newIntent(requireContext(), post)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }

    override fun openProfile(id: String?) {
        val intent = ProfileActivity.newIntent(requireContext(), id)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }


    companion object {
        private const val REQUEST_CODE_DETAIL = 1002
        private const val REQUEST_CODE_PROFILE = 1003
    }
}
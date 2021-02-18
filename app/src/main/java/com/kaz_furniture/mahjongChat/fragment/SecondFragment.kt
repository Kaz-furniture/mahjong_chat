package com.kaz_furniture.mahjongChat.fragment

import android.content.Intent
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
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.activity.PostDetailActivity
import com.kaz_furniture.mahjongChat.activity.ProfileActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.FragmentSecondBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class SecondFragment : Fragment(R.layout.fragment_second), PostListAdapter.Callback {

    private var binding: FragmentSecondBinding? = null
    private lateinit var adapter: PostListAdapter
    lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentSecondBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return
        val displayList = ArrayList<Post>().apply {
            addAll(allPostList.filter { it.deletedAt == null && myUser.followingUserIds.contains(it.userId) })
            addAll(allPostList.filter { it.userId == myUser.userId })
            sortByDescending { it.createdAt }
        }
        adapter = PostListAdapter(layoutInflater, displayList, this)
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        )
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        viewModel.loadPostList()
        bindingData.swipeRefresh.setOnRefreshListener {
            binding?.swipeRefresh?.isRefreshing = true
            viewModel.loadPostList()
            adapter.notifyDataSetChanged()
            binding?.swipeRefresh?.isRefreshing = false
        }
        binding?.fab?.setOnClickListener {
            launchPostActivity()
        }
        viewModel.updateData.observe(viewLifecycleOwner, Observer {
            viewModel.loadPostList()
        })
        viewModel.updatedList.observe(viewLifecycleOwner, Observer {
            val list = ArrayList<Post>().apply {
                addAll(it.filter { value ->  myUser.followingUserIds.contains(value.userId) })
                addAll(it.filter { value -> value.userId == myUser.userId })
                sortByDescending { value -> value.createdAt }
            }
            adapter.refresh(list)
        })
    }

    private fun launchPostActivity() {
        val intent = PostActivity.newIntent(requireContext())
        startActivityForResult(intent, REQUEST_CODE_POST)
    }

    override fun openDetail(post: Post) {
        val intent = PostDetailActivity.newIntent(requireContext(), post)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }

    override fun openProfile(id: String?) {
        val intent = ProfileActivity.newIntent(requireContext(), id)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_POST) {
            viewModel.loadPostList()
            Toast.makeText(requireContext(), "UPDATE!!!!", Toast.LENGTH_SHORT).show()
        }
        if (requestCode == REQUEST_CODE_PROFILE) {
            viewModel.loadPostList()
            Toast.makeText(requireContext(), "UPDATE!!!!", Toast.LENGTH_SHORT).show()
        }
        if (requestCode == REQUEST_CODE_DETAIL) {
            viewModel.loadPostList()
            Toast.makeText(requireContext(), "UPDATE!!!!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_POST = 1000
        private const val REQUEST_CODE_DETAIL = 1002
        private const val REQUEST_CODE_PROFILE = 1003
    }
}
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
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.activity.PostDetailActivity
import com.kaz_furniture.mahjongChat.activity.ProfileActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.FragmentHomeBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class HomeFragment : Fragment(R.layout.fragment_home), PostListAdapter.Callback {

    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: PostListAdapter
    lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentHomeBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return
        adapter = PostListAdapter(layoutInflater, allPostList.filter { it.deletedAt == null } as ArrayList<Post>, this)
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
            binding?.swipeRefresh?.isRefreshing = false
        }
        binding?.fab?.setOnClickListener{
            launchPostActivity()
        }
        viewModel.updateData.observe(viewLifecycleOwner, Observer {
            viewModel.loadPostList()
        })
        viewModel.updatedList.observe(viewLifecycleOwner, Observer {
            adapter.refresh(it)
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
package com.kaz_furniture.mahjongChat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.PostListAdapter
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.FragmentHomeBinding
import com.kaz_furniture.mahjongChat.databinding.FragmentSecondBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class SecondFragment : Fragment(R.layout.fragment_second) {

    private var binding: FragmentSecondBinding? = null
    private lateinit var adapter: PostListAdapter
    private val postList = ArrayList<Post>()
    lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentSecondBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return
        adapter = PostListAdapter(layoutInflater, postList)
        loadList()
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        )
        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }
        bindingData.swipeRefresh.setOnRefreshListener {
            loadList()
        }
        viewModel.updateData.observe(viewLifecycleOwner, Observer {
            loadList()
        })
    }

    private fun loadList() {
        binding?.swipeRefresh?.isRefreshing = true
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy(Post::createdAt.name, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(Post::class.java)
                        postList.clear()
                        if (fetchedList == null) {
                            Toast.makeText(MahjongChatApplication.applicationContext, "NO POST", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            postList.addAll(fetchedList)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
        binding?.swipeRefresh?.isRefreshing = false
    }
}
package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ListChoicePostDetailBinding
import com.kaz_furniture.mahjongChat.databinding.ListItemProfileBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel
import com.kaz_furniture.mahjongChat.viewModel.ProfileViewModel

class ChoicesView: RecyclerView {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
    }

    class Adapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewModel: PostDetailViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<Choice>()

        fun refresh(list: List<Choice>) {

            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_cell, null, false))
                ItemViewHolder(ListChoicePostDetailBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder is ItemViewHolder)
                onBindViewHolder(holder, position)
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.choice = data
//            holder.binding.tileImageView.setOnClickListener {
//                viewModel.choiceSelect(data)
//            }
//            holder.binding.wayText.setOnClickListener {
//                viewModel.choiceSelect(data)
//            }
            holder.binding.childView.setOnClickListener {
                viewModel.choiceSelect(data)
            }
        }

        class ItemViewHolder(val binding: ListChoicePostDetailBinding): RecyclerView.ViewHolder(binding.root)
    }
}
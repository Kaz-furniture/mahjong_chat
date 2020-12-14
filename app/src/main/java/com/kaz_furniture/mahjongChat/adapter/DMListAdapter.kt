package com.kaz_furniture.mahjongChat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.DM
import com.kaz_furniture.mahjongChat.databinding.ListDMBinding

class DMListAdapter (
        private val layoutInflater: LayoutInflater,
        private val dMList: List<String>
        ): RecyclerView.Adapter<DMListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dMList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListDMBinding>(
                layoutInflater,
                R.layout.list_d_m,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dMList[position])
    }

    class ViewHolder(
            private val binding: ListDMBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(name: String) {
            binding.DMUserName.text = name
        }
    }

}
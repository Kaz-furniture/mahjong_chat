package com.kaz_furniture.mahjongChat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Tile
import com.kaz_furniture.mahjongChat.databinding.ListTileBinding

class TileListAdapter(
        private val layoutInflater: LayoutInflater,
        private val tileList: List<Tile>
): RecyclerView.Adapter<TileListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return tileList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListTileBinding>(
                layoutInflater,
                R.layout.list_tile,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tileList[position])
    }


    class ViewHolder(
            private val binding: ListTileBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(tile: Tile) {

        }

    }
}
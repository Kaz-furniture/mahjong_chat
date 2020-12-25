package com.kaz_furniture.mahjongChat

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.data.Tile
import com.kaz_furniture.mahjongChat.viewModel.PostViewModel

//class TilesView: RecyclerView {
//
//    constructor(ctx: Context) : super(ctx)
//    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
//    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)
//
//    val customAdapter by lazy { Adapter(context) }
//
//    init {
//        adapter = customAdapter
//        setHasFixedSize(true)
//        layoutManager = GridLayoutManager(context, MAX_SPAN_COUNT).apply {
//            spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
//                override fun getSpanSize(position: Int): Int {
//                    return if (customAdapter.getItemViewType(position) == VIEW_TYPE_ITEM)
//                        UNIT_SPAN_COUNT
//                    else
//                        MAX_SPAN_COUNT
//                }
//            }
//        }
//    }
//
//    class Adapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//        private val viewModel: PostViewModel by (context as ComponentActivity).viewModels()
//        private val items = mutableListOf<Tile>()
//        private var indexImageIdMap = mapOf<Int, Int>()
//
////        private var selectedTile = Tile()
////            set(value) {
////                if (field.imageId == value.imageId)
////                    return
////                val oldImageId = field.imageId
////                val newImageId = value.imageId
////                field = value
////                indexImageIdMap[oldImageId]?.also {
////                    notifyItemChanged(it)
////                }
////                indexImageIdMap[newImageId]?.also {
////                    notifyItemChanged(it)
////                }
////            }
//
////        init {
////            viewModel.apply {
////                selectedTile.observe(context as ComponentActivity, Observer {
////                    this@Adapter.selectedTile = it
////                })
////            }
////        }
//
//        fun refresh(list: List<Tile>) {
//            items.apply {
//                clear()
//                addAll(list)
//            }
//            indexImageIdMap = list.withIndex().map { Pair(it.value.imageId, it.index) }.toMap()
//            notifyDataSetChanged()
//        }
//
//        override fun getItemCount(): Int = items.size
//
//        override fun getItemViewType(position: Int): Int = VIEW_TYPE_ITEM
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//                ItemViewHolder(TileItemCellBinding.inflate(LayoutInflater.from(context), parent, false))
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            when(holder) {
//                is SectionViewHolder -> onBindViewHolder(holder, position)
//                is ItemViewHolder -> onBindViewHolder(holder, position)
//            }
//        }
//
//        private fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
//            val data = items[position]
//            holder.binding.tile = data
//        }
//
//        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//            val data = items[position]
//            holder.binding.apply {
//                tile = data
//                isSelected = data.hasTile && data.imageId == selectedTile.imageId
//                clickView.setOnClickListener {
//                    viewModel.selectTile(data)
//                }
//            }
//        }
//
//        class SectionViewHolder(val binding: TileSectionCellBinding): RecyclerView.ViewHolder(binding.root)
//        class ItemViewHolder(val binding: TileItemCellBinding): RecyclerView.ViewHolder(binding.root)
//    }
//
//    companion object {
//        private const val MAX_SPAN_COUNT = 5
//        private const val UNIT_SPAN_COUNT = 1
//        private const val VIEW_TYPE_ITEM = 1
//        private const val VIEW_TYPE_SECTION = 2
//    }
//}
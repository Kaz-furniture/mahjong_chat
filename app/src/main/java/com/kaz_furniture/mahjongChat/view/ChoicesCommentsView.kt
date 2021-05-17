package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.constraintlayout.motion.utils.Easing
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.PostDetailActivity
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.databinding.*
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class ChoicesCommentsView: RecyclerView {

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
        private val items = mutableListOf<ChoiceCommentData>()
        private var isSelected = false
            set(value) {
                field = value
                notifyChoiceChanged()
            }

        init {
            viewModel.apply {
                isSelectedLiveData.observe(context as ComponentActivity, Observer {
                    isSelected = it
                })
            }
        }

        fun refresh(list: List<ChoiceCommentData>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        private fun notifyChoiceChanged() {
            for (i in items.filterNot { it.choice == null }.indices) {
                notifyItemChanged(i)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getItemViewType(position: Int): Int {
            return when {
                items[position].comment != null -> VIEW_TYPE_COMMENT
                items[position].supplement != null -> VIEW_TYPE_SUPPLEMENT
                items[position].choice?.userIds?.contains(myUser.userId) == true -> VIEW_TYPE_SELECTED
                else -> VIEW_TYPE_CHOICE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_cell, null, false))
                when (viewType) {
                    VIEW_TYPE_CHOICE -> ChoiceViewHolder(ListChoicePostDetailBinding.inflate(LayoutInflater.from(context), parent, false))
                    VIEW_TYPE_COMMENT -> CommentViewHolder(ListCommentBinding.inflate(LayoutInflater.from(context), parent, false))
                    VIEW_TYPE_SUPPLEMENT -> SupplementViewHolder(ListChoiceSupplementBinding.inflate(LayoutInflater.from(context), parent, false))
                    else -> SelectedViewHolder(ListChoiceSelectedBinding.inflate(LayoutInflater.from(context), parent, false))
                }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (holder) {
                is ChoiceViewHolder -> onBindViewHolder(holder, position)
                is CommentViewHolder -> onBindViewHolder(holder, position)
                is SelectedViewHolder -> onBindViewHolder(holder, position)
                is SupplementViewHolder -> onBindViewHolder(holder)
            }
        }

        private fun onBindViewHolder(holder: SupplementViewHolder) {
            holder.binding.allNumberText.text = context.getString(R.string.allNumber, viewModel.allUsersNumber)
        }

        private fun onBindViewHolder(holder: SelectedViewHolder, position: Int) {
            if (!isSelected) {
                holder.binding.apply {
                    barChart.visibility = View.INVISIBLE
                    percentText.visibility = View.INVISIBLE
                }
            } else {
                holder.binding.apply {
                    barChart.visibility = View.VISIBLE
                    percentText.visibility = View.VISIBLE
                }
            }
            val data = items[position].choice ?:return
            val percent = data.userIds.size * 100 / viewModel.allUsersNumber
            barChartSetting(percent, holder.binding.barChart, true)
            holder.binding.apply {
                choice = data
                childView.setOnClickListener {
                    if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty()) {
                        Toast.makeText(context, "ログインしてください", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.choiceSelect(data)
                }
                percentText.text = context.getString(R.string.percent, percent)
            }
        }

        private fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            val data = items[position].comment ?:return
            holder.binding.apply {
                imageId = MahjongChatApplication.allUserList.firstOrNull { it.userId == data.userId }?.imageUrl ?:""
                content = data.content
                userName = MahjongChatApplication.allUserList.firstOrNull { it.userId == data.userId }?.name ?:""
                commentTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time2), data.createdAt)
                userIcon.setOnClickListener {
                    viewModel.openProfile(data.userId)
                }
            }
        }

        private fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
            if (!isSelected) {
                holder.binding.apply {
                    barChart.visibility = View.INVISIBLE
                    percentText.visibility = View.INVISIBLE
                }
            } else {
                holder.binding.apply {
                    barChart.visibility = View.VISIBLE
                    percentText.visibility = View.VISIBLE
                }
            }
            val data = items[position].choice ?:return
            val percent = if (viewModel.allUsersNumber != 0) {
                data.userIds.size * 100 / viewModel.allUsersNumber
            } else 0
            barChartSetting(percent, holder.binding.barChart, false)
            holder.binding.apply {
                choice = data
                childView.setOnClickListener {
                    if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty()) {
                        Toast.makeText(context, "ログインしてください", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.choiceSelect(data)
                }
                percentText.text = context.getString(R.string.percent, percent)
            }
        }

        private fun barChartSetting(percent: Int, barChart: BarChart, selected: Boolean) {
            var entryList = ArrayList<BarEntry>().apply {
                add(BarEntry(1F, percent.toFloat()))
            }
            val barDataSetsList = ArrayList<IBarDataSet>().apply {
                val barDataSet = BarDataSet(entryList, "")
                barDataSet.color = if (selected) Color.BLACK else Color.WHITE
                barDataSet.setDrawValues(false)
                add(barDataSet)
            }
//            val barChart = holder.binding.barChart
            barChart.apply {
                data = BarData(barDataSetsList)
                xAxis.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.isEnabled = false
                axisLeft.axisMinimum = 0F
                axisLeft.axisMaximum = 100F
                axisRight.isEnabled = false
                isScaleXEnabled = false
                isScaleYEnabled = false
                animateY(200)
                setTouchEnabled(false)
                setDrawValueAboveBar(false)
            }
            barChart.invalidate()
        }

        class ChoiceCommentData {
            var choice: Choice? = null
            var supplement: Int? = null
            var comment: Comment? = null
        }

        class ChoiceViewHolder(val binding: ListChoicePostDetailBinding): RecyclerView.ViewHolder(binding.root)
        class CommentViewHolder(val binding: ListCommentBinding): RecyclerView.ViewHolder(binding.root)
        class SelectedViewHolder(val binding: ListChoiceSelectedBinding): RecyclerView.ViewHolder(binding.root)
        class SupplementViewHolder(val binding: ListChoiceSupplementBinding): RecyclerView.ViewHolder(binding.root)

        companion object {
            private const val VIEW_TYPE_CHOICE = 0
            private const val VIEW_TYPE_COMMENT = 1
            private const val VIEW_TYPE_SELECTED = 2
            private const val VIEW_TYPE_SUPPLEMENT = 3
        }
    }
}
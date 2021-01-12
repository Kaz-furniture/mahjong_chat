package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.databinding.ListChoicePostDetailBinding
import com.kaz_furniture.mahjongChat.databinding.ListCommentBinding
import com.kaz_furniture.mahjongChat.databinding.ListEmptyChoicePostDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel
import timber.log.Timber

class ChoicesCommentsView : RecyclerView {

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
                notifyChoices()
            }

        init {
            viewModel.apply {
                isSelectedLiveData.observe(context as ComponentActivity, Observer {
                    isSelected = it
                })
            }
        }

        fun refresh(list: List<ChoiceCommentData>) {
            Timber.d("refresh list.size:${list.size}")
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        private fun notifyChoices() {
            for (i in items.filterNot { it.choice == null }.indices)
                notifyItemChanged(i)
        }

        override fun getItemViewType(position: Int): Int =
                if (items[position].comment != null)
                    VIEW_TYPE_COMMENT
                else if (isSelected) {
                    if (items[position].choice?.userIds?.contains(myUser.userId) == true)
                        VIEW_TYPE_CHOICE
                    else
                        VIEW_TYPE_EMPTY_CHOICE
                } else
                    VIEW_TYPE_CHOICE

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  {
               return if (viewType == VIEW_TYPE_CHOICE)
                    ChoiceViewHolder(ListChoicePostDetailBinding.inflate(LayoutInflater.from(context), parent, false))
                else if (viewType == VIEW_TYPE_EMPTY_CHOICE)
                    EmptyChoiceViewHolder(ListEmptyChoicePostDetailBinding.inflate(LayoutInflater.from(context), parent, false))
                else
                    CommentViewHolder(ListCommentBinding.inflate(LayoutInflater.from(context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Timber.d("onBindViewHolder holder:$holder position:$position")
            when (holder) {
                is ChoiceViewHolder -> onBindViewHolder(holder, position)
                is CommentViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
            Timber.d("onBindViewHolder choice data:${items[position].choice}")
            val data = items[position].choice ?: return
            holder.binding.apply {
                choice = data
                childView.setOnClickListener {
                    viewModel.choiceSelect(data)
                }
            }
        }

        private fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            Timber.d("onBindViewHolder comment data:${items[position].comment}")
            val data = items[position].comment ?: return
            holder.binding.userId = data.userId
            holder.binding.content = data.content
            holder.binding.userName = MahjongChatApplication.allUserList.filter { it.userId == data.userId }[0].name
        }

        class ChoiceCommentData {
            var choice: Choice? = null
            var comment: Comment? = null
        }

        class ChoiceViewHolder(val binding: ListChoicePostDetailBinding): RecyclerView.ViewHolder(binding.root)
        class CommentViewHolder(val binding: ListCommentBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyChoiceViewHolder(val binding: ListEmptyChoicePostDetailBinding): RecyclerView.ViewHolder(binding.root)

        companion object {
            private const val VIEW_TYPE_CHOICE = 0
            private const val VIEW_TYPE_COMMENT = 1
            private const val VIEW_TYPE_EMPTY_CHOICE = 2
        }
    }
}
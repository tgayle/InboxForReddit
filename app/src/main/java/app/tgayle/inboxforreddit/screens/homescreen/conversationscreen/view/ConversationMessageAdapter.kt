package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.conversation_message_rv_item.*
import java.text.SimpleDateFormat

typealias OnHideRevealButtonPressed = (message: ConversationRecyclerViewItem, adapterPosition: Int) -> Unit

class ConversationMessageAdapter: RecyclerView.Adapter<ConversationMessageAdapter.ConversationMessageViewholder>() {
    private val items = mutableListOf<ConversationRecyclerViewItem>()

    override fun getItemCount(): Int = items.size

    var onHideRevealButtonPressed: OnHideRevealButtonPressed? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationMessageViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_message_rv_item, parent, false)
        return ConversationMessageViewholder(view)
    }

    override fun onBindViewHolder(holder: ConversationMessageViewholder, position: Int) {
        holder.bind(items[position], onHideRevealButtonPressed)
    }

    fun submitItems(newItems: List<ConversationRecyclerViewItem>) {
        val diffUtilResult = DiffUtil.calculateDiff(ConversationDiffUtil(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun updateItem(newItemState: ConversationRecyclerViewItem, position: Int) {
        items[position] = newItemState
        notifyItemChanged(position)
    }

    class ConversationMessageViewholder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        var currentMessage: ConversationRecyclerViewItem? = null

        fun bind(item: ConversationRecyclerViewItem?, onHideRevealButtonPressed: OnHideRevealButtonPressed?) {
            if (item == null) {
                clear()
                return
            }
            currentMessage = item
            val message = item.message

            conversation_message_item_correspondent.text = message.author
            conversation_message_item_body.text = message.body
            conversation_message_item_date.text = SimpleDateFormat.getDateInstance().format(message.created)

            containerView.transitionName = "root_${message.fullName}"
            conversation_message_item_correspondent.transitionName = "author_${message.fullName}"
            conversation_message_item_body.transitionName = "body_${message.fullName}"
            conversation_message_item_date.transitionName = "date_${message.fullName}"

            conversation_message_item_hidereveal.setOnClickListener {
                currentMessage.let { if (it != null) onHideRevealButtonPressed?.invoke(it, adapterPosition) }
            }

            conversation_message_item_body.maxLines = if (item.isCollapsed) 1 else Int.MAX_VALUE
            conversation_message_item_body.alpha = if (item.isCollapsed) 0.63f else 1f
        }

        private fun clear() {
            currentMessage = null
            conversation_message_item_correspondent.text = null
            conversation_message_item_body.text = null
            conversation_message_item_date.text = null
        }
    }

    class ConversationDiffUtil(val oldItems: List<ConversationRecyclerViewItem>, val newItems: List<ConversationRecyclerViewItem>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return DIFF_UTIL.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return DIFF_UTIL.areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<ConversationRecyclerViewItem>() {
            override fun areItemsTheSame(oldItem: ConversationRecyclerViewItem,
                                         newItem: ConversationRecyclerViewItem
            ): Boolean {
                return RedditMessage.DEFAULT_DIFF_UTIL.areItemsTheSame(oldItem.message, newItem.message)
            }

            override fun areContentsTheSame(oldItem: ConversationRecyclerViewItem,
                                            newItem: ConversationRecyclerViewItem): Boolean {
                return RedditMessage.DEFAULT_DIFF_UTIL.areContentsTheSame(oldItem.message, newItem.message) &&
                        oldItem.isCollapsed == newItem.isCollapsed
            }
        }
    }
}
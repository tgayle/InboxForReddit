package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.util.getTimeAgo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.conversation_message_rv_item.*


class ConversationMessageAdapter: ListAdapter<RedditMessage, ConversationMessageAdapter.ConversationMessageViewholder>(RedditMessage.DEFAULT_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationMessageViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_message_rv_item, parent, false)
        return ConversationMessageViewholder(view)
    }

    override fun onBindViewHolder(holder: ConversationMessageViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationMessageViewholder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        var isCollapsed = false

        fun bind(message: RedditMessage?) {
            if (message == null) {
                clear()
                return
            }
            conversation_message_item_correspondent.text = message.author
            conversation_message_item_body.text = message.body
            conversation_message_item_date.text = getTimeAgo(message.created.time)

            containerView.transitionName = "root_${message.fullName}"
            conversation_message_item_correspondent.transitionName = "author_${message.fullName}"
            conversation_message_item_body.transitionName = "body_${message.fullName}"
            conversation_message_item_date.transitionName = "date_${message.fullName}"

            conversation_message_item_hidereveal.setOnClickListener {
                isCollapsed = !isCollapsed
                notifyItemChanged(adapterPosition)
            }

            conversation_message_item_body.maxLines = if (isCollapsed) 1 else Int.MAX_VALUE
            conversation_message_item_body.alpha = if (isCollapsed) 0.63f else 1f
        }

        private fun clear() {
            isCollapsed = false
            conversation_message_item_correspondent.text = null
            conversation_message_item_body.text = null
            conversation_message_item_date.text = null
        }
    }
}
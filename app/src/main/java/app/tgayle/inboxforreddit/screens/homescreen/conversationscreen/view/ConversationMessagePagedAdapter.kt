package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.conversation_message_rv_item.*
import java.text.SimpleDateFormat

class ConversationMessagePagedAdapter: PagedListAdapter<RedditMessage, ConversationMessagePagedAdapter.ConversationMessageViewholder>(RedditMessage.DEFAULT_DIFF_UTIL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationMessageViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_message_rv_item, parent, false)
        return ConversationMessageViewholder(view)
    }

    override fun onBindViewHolder(holder: ConversationMessageViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ConversationMessageViewholder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(message: RedditMessage?) {
            if (message == null) {
                clear()
                return
            }

            conversation_message_item_correspondent.text = message.author
            conversation_message_item_body.text = message.body
            conversation_message_item_date.text = SimpleDateFormat.getDateInstance().format(message.created)

        }

        private fun clear() {
            conversation_message_item_correspondent.text = null
            conversation_message_item_body.text = null
            conversation_message_item_date.text = null
        }
    }
}
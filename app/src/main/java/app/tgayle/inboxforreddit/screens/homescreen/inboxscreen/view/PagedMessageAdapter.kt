package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage

class PagedMessageAdapter: PagedListAdapter<RedditMessage, MessageViewHolder>(RedditMessage.DEFAULT_DIFF_UTIL) {
    var onMessageClickListener: OnMessageClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.message_rv_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position), onMessageClickListener)
    }
}
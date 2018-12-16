package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage

class PagedMessageAdapter: PagedListAdapter<RedditMessage, MessageRecyclerViewAdapter.MessageViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRecyclerViewAdapter.MessageViewHolder {
        return MessageRecyclerViewAdapter.MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.message_rv_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageRecyclerViewAdapter.MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<RedditMessage>() {
            override fun areItemsTheSame(oldItem: RedditMessage, newItem: RedditMessage) = oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: RedditMessage, newItem: RedditMessage) = oldItem == newItem
        }
    }
}
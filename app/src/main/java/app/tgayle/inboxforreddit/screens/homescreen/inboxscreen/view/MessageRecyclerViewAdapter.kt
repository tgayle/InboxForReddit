package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage
import java.text.SimpleDateFormat

class MessageRecyclerViewAdapter(private var items: List<RedditMessage> = arrayListOf()): RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_rv_item, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun resetItems(newItems: List<RedditMessage>) {
        val originalSize = items.size
        notifyItemRangeRemoved(0, originalSize)
        items = newItems
        notifyItemRangeInserted(0, items.size)
    }
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val authorView = itemView.findViewById<TextView>(R.id.message_rv_item_author)
        private val dateView = itemView.findViewById<TextView>(R.id.message_rv_item_date)
        private val messageView = itemView.findViewById<TextView>(R.id.message_rv_item_message)

        fun bind(redditMessage: RedditMessage) {
            authorView.text = "${redditMessage.author} x ${redditMessage.correspondent}"
            dateView.text = SimpleDateFormat.getDateInstance().format(redditMessage.created)
            messageView.text = redditMessage.body
        }
    }
}
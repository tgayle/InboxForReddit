package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        private val subjectView = itemView.findViewById<TextView>(R.id.message_rv_item_subject)
        private val sentReceivedImageView = itemView.findViewById<ImageView>(R.id.message_rv_item_sentreceived)

        fun bind(redditMessage: RedditMessage?) {
            if (redditMessage == null) {
                reset()
                return
            }

            redditMessage.run {
                authorView.text = correspondent
                dateView.text = SimpleDateFormat.getDateInstance().format(created)
                messageView.text = body
                subjectView.text = subject
                sentReceivedImageView.rotation = if (owner == author) 0F else 180F

                val primaryColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                val defaultTextColor = ContextCompat.getColor(itemView.context, R.color.defaultTextColor)

                if (unread) {
                    val bold = Typeface.DEFAULT_BOLD
                    authorView.typeface = bold
                    subjectView.typeface = bold
                    dateView.typeface = bold
                    dateView.setTextColor(primaryColor)
                    sentReceivedImageView.setColorFilter(primaryColor)
                } else {
                    val defaultTypeface = Typeface.DEFAULT
                    authorView.typeface = defaultTypeface
                    subjectView.typeface = defaultTypeface
                    dateView.typeface = defaultTypeface
                    dateView.setTextColor(defaultTextColor)
                    sentReceivedImageView.setColorFilter(null)
                }
            }
        }

        fun reset() {
            authorView.text = null
            dateView.text = null
            messageView.text = null
            subjectView.text = null
            sentReceivedImageView.rotation = 0F
        }
    }
}
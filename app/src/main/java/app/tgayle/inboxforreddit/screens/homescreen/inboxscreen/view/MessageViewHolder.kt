package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditMessage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.message_rv_item.*
import java.text.SimpleDateFormat

typealias OnMessageClickListener = (message: RedditMessage) -> Unit

class MessageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(redditMessage: RedditMessage?, onMessageClick: OnMessageClickListener?) {
        if (redditMessage == null) {
            reset()
            return
        }

        redditMessage.run {
            message_rv_item_author.text = correspondent
            message_rv_item_date.text = SimpleDateFormat.getDateInstance().format(created)
            message_rv_item_message.text = body
            message_rv_item_subject.text = subject
            message_rv_item_sentreceived.rotation = if (owner == author) 0F else 180F
            message_rv_item_root.setOnClickListener {
                onMessageClick?.invoke(this)
            }

            val primaryColor = ContextCompat.getColor(
                itemView.context,
                R.color.colorPrimary
            )
            val defaultTextColor = ContextCompat.getColor(
                itemView.context,
                R.color.defaultTextColor
            )

            if (unread) {
                val bold = Typeface.DEFAULT_BOLD
                message_rv_item_author.typeface = bold
                message_rv_item_subject.typeface = bold
                message_rv_item_date.typeface = bold
                message_rv_item_date.setTextColor(primaryColor)
                message_rv_item_sentreceived.setColorFilter(primaryColor)
            } else {
                val defaultTypeface = Typeface.DEFAULT
                message_rv_item_author.typeface = defaultTypeface
                message_rv_item_subject.typeface = defaultTypeface
                message_rv_item_date.typeface = defaultTypeface
                message_rv_item_date.setTextColor(defaultTextColor)
                message_rv_item_sentreceived.setColorFilter(null)
            }
        }
    }

    fun reset() {
        message_rv_item_author.text = null
        message_rv_item_date.text = null
        message_rv_item_message.text = null
        message_rv_item_subject.text = null
        message_rv_item_sentreceived.rotation = 0F
        message_rv_item_root.setOnClickListener(null)
    }
}
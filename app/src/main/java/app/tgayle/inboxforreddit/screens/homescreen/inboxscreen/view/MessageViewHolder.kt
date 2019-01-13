package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view

import android.graphics.Typeface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.util.getColorFromAttr
import app.tgayle.inboxforreddit.util.getTimeAgo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.message_rv_item.*


typealias OnMessageClickListener = (message: RedditMessage) -> Unit

class MessageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(redditMessage: RedditMessage?, onMessageClick: OnMessageClickListener?) {
        if (redditMessage == null) {
            reset()
            return
        }

        redditMessage.run {
            message_rv_item_author.text = correspondent
            message_rv_item_date.text = getTimeAgo(created.time)
            message_rv_item_message.text = body
            message_rv_item_subject.text = subject
            message_rv_item_sentreceived.rotation = if (owner == author) 0F else 180F
            message_rv_item_root.setOnClickListener {
                onMessageClick?.invoke(this)
            }

            val accentColor = itemView.context.getColorFromAttr(android.R.attr.colorAccent)
            val defaultTextColor = itemView.context.getColorFromAttr(android.R.attr.textColor)

            if (unread) {
                val bold = Typeface.DEFAULT_BOLD
                message_rv_item_author.typeface = bold
                message_rv_item_subject.typeface = bold
                message_rv_item_date.typeface = bold
                message_rv_item_date.setTextColor(accentColor)
                message_rv_item_sentreceived.setColorFilter(accentColor)
            } else {
                val defaultTypeface = Typeface.DEFAULT
                message_rv_item_author.typeface = defaultTypeface
                message_rv_item_subject.typeface = defaultTypeface
                message_rv_item_date.typeface = defaultTypeface
                message_rv_item_date.setTextColor(defaultTextColor)
                message_rv_item_sentreceived.colorFilter = null
            }
        }
    }

    private fun reset() {
        message_rv_item_author.text = null
        message_rv_item_date.text = null
        message_rv_item_message.text = null
        message_rv_item_subject.text = null
        message_rv_item_sentreceived.rotation = 0F
        message_rv_item_root.setOnClickListener(null)
    }
}
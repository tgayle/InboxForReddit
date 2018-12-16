package app.tgayle.inboxforreddit.model

import androidx.room.Entity

@Entity
data class RedditUsernameAndUnreadMessageCount(val username: String, val numUnreadMessages: Int)
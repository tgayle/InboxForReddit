package app.tgayle.inboxforreddit.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class RedditAccount(@PrimaryKey val id: String, val name: String, val createdUtc: Date, val refreshToken: String)
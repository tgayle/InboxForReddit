package app.tgayle.inboxforreddit.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import app.tgayle.inboxforreddit.model.RedditMessage.DistinguishedState
import net.dean.jraw.models.DistinguishedStatus
import java.util.*

/**
 * A data class model representing a RedditMessage
 * @property uuid A unique identifer since there's a chance of having multiple messages with the same id.
 * @property owner The username of the user who this previewMessage belongs to.
 * @property author The username of the user who sent this previewMessage.
 * @property destination The username of the user who received this previewMessage.
 * @property unread A boolean representing whether or not this previewMessage has been read.
 * @property body The message text itself, in markdown format.
 * @property fullName The reddit id for this previewMessage, including its type (t1 -> comment, t4 -> private previewMessage, etc)
 * @property created The time the message was created.
 * @property distinguished A [DistinguishedState] representing the "importance" of the previewMessage, such as if it's sent by an admin.
 */
@Entity(tableName = "messages")
data class RedditMessage(
    val uuid: UUID,
    @ForeignKey(entity = RedditAccount::class, parentColumns = ["name"], childColumns = ["owner"])
    val owner: String,
    val author: String,
    val destination: String,
    val unread: Boolean,
    @PrimaryKey
    val fullName: String,
    val parentName: String,
    val created: Date,
    val subject: String,
    val body: String,
    val distinguished: DistinguishedState) {

    constructor(uuid: UUID,
                owner: String,
                author: String,
                destination: String,
                unread: Boolean,
                fullName: String,
                parentName: String,
                created: Date,
                subject: String,
                body: String,
                jrawDistinguished: DistinguishedStatus): this(uuid, owner, author, destination, unread, fullName, parentName, created, subject, body, DistinguishedState.resolveFrom(jrawDistinguished))

    @Ignore
    val correspondent: String = if (owner == author) destination else author

    /**
     * Compares equality of two RedditMessages. No need to check every single property though as things such as body and
     * created can never change.
     */
    override fun equals(other: Any?): Boolean {
        return if (other != null && other is RedditMessage) {
            return this.unread == other.unread &&
                    this.fullName == other.fullName &&
                    this.parentName == other.parentName &&
                    this.author == other.author &&
                    this.destination == other.destination
        } else false
    }

    enum class DistinguishedState {
        NORMAL,
        MODERATOR,
        ADMIN,
        GOLD,
        SPECIAL;

        companion object {
            fun resolveFrom(state: DistinguishedStatus) = when (state) {
                DistinguishedStatus.NORMAL -> NORMAL
                DistinguishedStatus.MODERATOR -> MODERATOR
                DistinguishedStatus.ADMIN -> ADMIN
                DistinguishedStatus.SPECIAL -> SPECIAL
                DistinguishedStatus.GOLD -> GOLD
            }
        }
    }

    /**
     * Base DIFF_UTIL for whenever messages should be compared.
     */
    companion object {
        val DEFAULT_DIFF_UTIL = object: DiffUtil.ItemCallback<RedditMessage>() {
            override fun areItemsTheSame(oldItem: RedditMessage, newItem: RedditMessage) = oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: RedditMessage, newItem: RedditMessage) = oldItem == newItem
        }
    }
}
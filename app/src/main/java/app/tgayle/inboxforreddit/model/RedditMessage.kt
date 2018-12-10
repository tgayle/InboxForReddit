package app.tgayle.inboxforreddit.model

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
open class RedditMessage(
    open val uuid: UUID,
    @ForeignKey(entity = RedditAccount::class, parentColumns = ["name"], childColumns = ["owner"])
    open val owner: String,
    open val author: String,
    open val destination: String,
    open val unread: Boolean,
    @PrimaryKey
    open val fullName: String,
    open val created: Date,
    open val body: String,
    open val distinguished: DistinguishedState) {

    constructor(uuid: UUID,
                owner: String,
                author: String,
                destination: String,
                unread: Boolean,
                fullName: String,
                created: Date,
                body: String,
                jrawDistinguished: DistinguishedStatus): this(uuid, owner, author, destination, unread, fullName, created, body, DistinguishedState.resolveFrom(jrawDistinguished))

    @Ignore
    open val correspondent: String = if (owner == author) destination else author

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
}
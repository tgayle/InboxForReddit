package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import java.util.*

@Dao
abstract class MessageDao {

    @Query("SELECT * FROM messages")
    abstract fun getAllMessages(): LiveData<List<RedditMessage>>

    @Query("SELECT * FROM messages WHERE owner = :username")
    abstract fun getUserMessages(username: String): LiveData<List<RedditMessage>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(messages: List<RedditMessage>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(message: RedditMessage): Long

    @Query("UPDATE messages SET unread = :newUnread WHERE uuid = :uuid")
    protected abstract fun insertReplacingOldMessage(uuid: UUID, newUnread: Boolean = false): Long

    fun upsert(messages: List<RedditMessage>): List<Long> {
        val insertionResults = insert(messages).toMutableList()
        insertionResults.forEachIndexed { index, result ->
            // -1 means item wasn't inserted.
            if (result != -1L) return@forEachIndexed
            messages[index].run {
                val upsertResult = insertReplacingOldMessage(uuid, unread)
                insertionResults[index] = upsertResult
            }
        }
        return insertionResults
    }

    @Query("SELECT * FROM messages WHERE owner = :user ORDER BY created DESC LIMIT 1")
    abstract fun getConversationPreviews(user: RedditAccount): LiveData<List<RedditMessage>>

}
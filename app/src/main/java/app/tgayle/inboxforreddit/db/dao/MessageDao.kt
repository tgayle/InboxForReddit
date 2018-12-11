package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.tgayle.inboxforreddit.model.RedditMessage

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

    @Query("UPDATE messages SET unread = :newUnread WHERE fullName = :fullName")
    protected abstract fun insertReplacingOldMessage(fullName: String, newUnread: Boolean = false): Long

    // Insert new message or update preexisting message.
    fun upsert(messages: List<RedditMessage>): List<Long> {
        val insertionResults = insert(messages).toMutableList()
        insertionResults.forEachIndexed { index, result ->
            // -1 means item wasn't inserted.
            if (result != -1L) return@forEachIndexed
            messages[index].run {
                val upsertResult = insertReplacingOldMessage(fullName, unread)
                insertionResults[index] = upsertResult
            }
        }
        return insertionResults
    }

    @Query("SELECT * " +
            "FROM messages " +
            "WHERE owner = :username " +
            "GROUP BY parentName " +
            "HAVING created = MAX(created) " +
            "ORDER BY created DESC")
    abstract fun getConversationPreviews(username: String): LiveData<List<RedditMessage>>

}
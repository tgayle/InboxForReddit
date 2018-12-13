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

    @Query("SELECT * FROM messages WHERE owner = :username AND author = :username ORDER BY created DESC")
    abstract fun getUserSentMessagesDesc(username: String): LiveData<List<RedditMessage>>

    @Query("SELECT * FROM messages WHERE owner = :username AND unread = 1 ORDER BY created DESC")
    abstract fun getUnreadMessagesDesc(username: String): LiveData<List<RedditMessage>>

    @Query("""
        SELECT *
        FROM messages
        WHERE owner = :username
        GROUP BY parentName
        HAVING created = MAX(created)
        AND (COUNT(CASE WHEN unread = 1 then 1 end) >= 1)
        ORDER BY created DESC
    """)
    abstract fun getConversationsWithUnreadMessages(username: String): LiveData<List<RedditMessage>>

    @Query("SELECT * FROM messages WHERE owner = :username AND unread = 1")
    abstract fun getUnreadMessagesSync(username: String): List<RedditMessage>

    @Query("SELECT * FROM messages WHERE owner = :username AND unread = 1 ORDER BY created ASC LIMIT 1")
    abstract fun getOldestUnreadMessageSync(username: String): RedditMessage

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(messages: List<RedditMessage>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(message: RedditMessage): Long

    @Query("UPDATE messages SET unread = :newUnread WHERE fullName = :fullName")
    protected abstract fun insertReplacingOldMessage(fullName: String, newUnread: Boolean = false): Long

    // Insert new message or update preexisting message.
    fun upsert(messages: List<RedditMessage>): List<Long> {
        val insertionResults = insert(messages).toMutableList()

        for ((index, result) in insertionResults.withIndex()) {
            // -1 means item wasn't inserted.
            if (result != -1L) continue

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

    @Query("SELECT COUNT(*) FROM messages WHERE owner = :username")
    abstract fun getUserMessageCount(username: String): Int

    @Query("SELECT * FROM messages WHERE owner = :username AND author = :username ORDER BY created DESC LIMIT 1")
    abstract fun getNewestSentUserMessageSync(username: String): RedditMessage

    @Query("SELECT * FROM messages WHERE owner = :username AND destination = :username ORDER BY created DESC LIMIT 1")
    abstract fun getNewestReceivedUserMessageSync(username: String): RedditMessage
}
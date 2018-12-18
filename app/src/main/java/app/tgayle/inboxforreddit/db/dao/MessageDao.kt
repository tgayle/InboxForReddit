package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount

@Dao
abstract class MessageDao {

    companion object {
        const val ALL_MESSAGES = "SELECT * FROM messages"
        const val ALL_USER_MESSAGES = "SELECT * FROM messages WHERE owner = :username"
        const val ALL_USER_SENT_MESSAGES = "SELECT * FROM messages WHERE owner = :username AND author = :username ORDER BY created DESC"
        const val ALL_UNREAD_USER_MESSAGES = "SELECT * FROM messages WHERE owner = :username AND unread = 1"
        const val ALL_UNREAD_USER_MESSAGES_DESC = "SELECT * FROM messages WHERE owner = :username AND unread = 1 ORDER BY created DESC"
        const val OLDEST_UNREAD_USER_MESSAGES = "SELECT * FROM messages WHERE owner = :username AND unread = 1 ORDER BY created ASC LIMIT 1"
        const val UPDATE_MESSAGE_UNREAD_VALUE = "UPDATE messages SET unread = :newUnread WHERE fullName = :fullName"
        const val COUNT_USER_MESSAGES = "SELECT COUNT(*) FROM messages WHERE owner = :username"
        const val NEWEST_USER_SENT_MESSAGE = "SELECT * FROM messages WHERE owner = :username AND author = :username ORDER BY created DESC LIMIT 1"
        const val NEWEST_USER_RECEIVED_MESSAGE = "SELECT * FROM messages WHERE owner = :username AND destination = :username ORDER BY created DESC LIMIT 1"
        const val NEWEST_USER_SENT_MESSAGE_NAME = "SELECT fullName FROM messages WHERE owner = :username AND author = :username ORDER BY created DESC LIMIT 1"
        const val NEWEST_USER_RECEIVED_MESSAGE_NAME = "SELECT fullName FROM messages WHERE owner = :username AND destination = :username ORDER BY created DESC LIMIT 1"
        const val OLDEST_UNREAD_USER_MESSAGE_NAME = "SELECT fullName FROM messages WHERE owner = :username AND unread = 1 ORDER BY created ASC LIMIT 1"
        const val DELETE_USER_MESSAGES = "DELETE FROM messages WHERE owner = :username"

        const val ALL_USER_CONVERSATIONS_DESC = """
            SELECT *
            FROM messages
            WHERE owner = :username
            GROUP BY parentName
            HAVING created = MAX(created)
            ORDER BY created DESC
        """

        const val ALL_CONVERSATIONS_WITH_UNREAD_MESSAGES = """
            SELECT *
            FROM messages
            WHERE owner = :username
            GROUP BY parentName
            HAVING created = MAX(created)
            AND (COUNT(CASE WHEN unread = 1 then 1 end) >= 1)
            ORDER BY created DESC
        """
    }

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

    @Query(ALL_MESSAGES)
    abstract fun getAllMessages(): LiveData<List<RedditMessage>>

    @Query(ALL_USER_MESSAGES)
    abstract fun getUserMessages(username: String?): LiveData<List<RedditMessage>>

    @Query(ALL_USER_SENT_MESSAGES)
    abstract fun getUserSentMessagesDesc(username: String?): LiveData<List<RedditMessage>>

    @Query(ALL_USER_SENT_MESSAGES)
    abstract fun getUserSentMessagesDescDataSource(username: String?): DataSource.Factory<Int, RedditMessage>

    @Query(ALL_UNREAD_USER_MESSAGES_DESC)
    abstract fun getUnreadMessagesDesc(username: String?): LiveData<List<RedditMessage>>

    @Query(ALL_CONVERSATIONS_WITH_UNREAD_MESSAGES)
    abstract fun getConversationsWithUnreadMessages(username: String?): LiveData<List<RedditMessage>>

    @Query(ALL_CONVERSATIONS_WITH_UNREAD_MESSAGES)
    abstract fun getConversationsWithUnreadMessagesDataSource(username: String?): DataSource.Factory<Int, RedditMessage>

    @Query(ALL_UNREAD_USER_MESSAGES)
    abstract fun getUnreadMessagesSync(username: String): List<RedditMessage>

    @Query(OLDEST_UNREAD_USER_MESSAGES)
    abstract fun getOldestUnreadMessageSync(username: String?): RedditMessage?

    @Query(OLDEST_UNREAD_USER_MESSAGE_NAME)
    abstract fun getOldestUnreadMessageNameSync(username: String?): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(messages: List<RedditMessage>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(message: RedditMessage): Long

    @Query(UPDATE_MESSAGE_UNREAD_VALUE)
    protected abstract fun insertReplacingOldMessage(fullName: String, newUnread: Boolean = false): Long

    @Query(ALL_USER_CONVERSATIONS_DESC)
    abstract fun getConversationPreviews(username: String?): LiveData<List<RedditMessage>>

    @Query(ALL_USER_CONVERSATIONS_DESC)
    abstract fun getConversationPreviewDataSource(username: String?): DataSource.Factory<Int, RedditMessage>

    @Query(COUNT_USER_MESSAGES)
    abstract fun getUserMessageCount(username: String?): Int

    @Query(NEWEST_USER_SENT_MESSAGE)
    abstract fun getNewestSentUserMessageSync(username: String?): RedditMessage

    @Query(NEWEST_USER_SENT_MESSAGE_NAME)
    abstract fun getNewestSentUserMessageNameSync(username: String?): String

    @Query(NEWEST_USER_RECEIVED_MESSAGE)
    abstract fun getNewestReceivedUserMessageSync(username: String?): RedditMessage

    @Query(NEWEST_USER_RECEIVED_MESSAGE_NAME)
    abstract fun getNewestReceivedUserMessageNameSync(username: String?): String

    @Query("SELECT owner username, SUM(unread = 1) numUnreadMessages FROM messages GROUP BY owner")
    abstract fun getUsernamesAndUnreadMessageCountsForEach(): DataSource.Factory<Int, RedditUsernameAndUnreadMessageCount>

    @Query(DELETE_USER_MESSAGES)
    abstract fun removeMessagesWithOwner(username: String?)

    @Query ("""
        SELECT *
        FROM messages
        WHERE parentName = :parentName
        ORDER BY created ASC
    """)
    abstract fun getConversationMessages(parentName: String?): DataSource.Factory<Int, RedditMessage>

    @Query("""
        SELECT *
        FROM messages
        WHERE parentName = :parentName
        ORDER BY created ASC
        LIMIT 1
    """)
    abstract fun getFirstMessageOfConversation(parentName: String?): RedditMessage?
}
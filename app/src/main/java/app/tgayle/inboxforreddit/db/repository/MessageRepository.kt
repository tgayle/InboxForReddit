package app.tgayle.inboxforreddit.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.model.MessageFilterOption
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditClientAccountPair
import app.tgayle.inboxforreddit.model.RedditMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Message
import javax.inject.Inject

@InboxApplicationScope
class MessageRepository @Inject constructor(private val appDatabase: AppDatabase) {

    /**
     * Saves a list of messages locally.
     */
    private fun saveMessages(messages: List<RedditMessage>) = GlobalScope.async {
        val result = appDatabase.messages().upsert(messages)
        Log.d("Data Repo", "Just saved ${result.size} messages.")
        return@async result
    }

    /**
     * Returns LiveData containing a user's messages.
     * @param user The user whose messages should be retrieved.
     */
    fun getMessages(user: LiveData<RedditClientAccountPair>) = Transformations.switchMap(user) {
        appDatabase.messages().getUserMessages(it.account?.name)
    }

    /**
     * Returns LiveData containing a list of user conversations. Each "conversation" in this list is a [RedditMessage],
     * and each message is the oldest for that unique first_message_name, effectively giving us the newest message for
     * that conversation.
     *
     * @param user The user whose messages should be retrieved.
     */
    fun getInbox(user: LiveData<RedditAccount>) = Transformations.switchMap(user) {
        appDatabase.messages().getConversationPreviews(it.name)
    }

    /**
     * Refreshes user messages, either loading all past messages if the user has 0 messages stored locally, or loading
     * the most recent messages if the user already has messages stored locally.
     * @param client A client for making reddit requests
     * @param account The locally stored user that these messages are for.
     */
    suspend fun refreshMessages(client: RedditClient?, account: RedditAccount): List<Long> {
        /* Ignore requests when the client is null */
        if (client == null) {
            Log.d("Data Repo", "Request ignored since reddit client was null.")
            return emptyList()
        }
        Log.d("DataRepo", "Starting load...")
        client.autoRenew = true

        val numPreexistingUserMessages = appDatabase.messages().getUserMessageCount(account.name)
        Log.d("Data Repo", "Message # for ${account.name} is $numPreexistingUserMessages")

        return if (numPreexistingUserMessages == 0) {
            loadAllPastMessages(client, account)
        } else {
            loadNewestMessages(client, account)
        }
    }

    /**
     * Loads a user's newest private messages, running lazily loading each page until we find a message we already have locally
     * stored.
     * @param client The client to use for retrieving messages
     * @param account The user who these messages should belong to upon saving.
     */
    private suspend fun loadNewestMessages(client: RedditClient, account: RedditAccount): List<Long> {
        Log.d("Data Repo", "Attempting to load newest messages...")
        val newestSentMessageName = appDatabase.messages().getNewestSentUserMessageNameSync(account.name)
        /*
        Get oldest unread message to make sure we update messages that are unread. This is necessary since JRAW doesn't
        currently support getting a specific message or adding a before/after query, so just load more messages to make
        sure older messages are updated. If there's no unread messages then just start from the newest received database message.
         */
        val oldestUnreadMessageName = appDatabase.messages().getOldestUnreadMessageNameSync(account.name) ?: appDatabase.messages().getNewestReceivedUserMessageNameSync(account.name)
        /*
        Be lazy about loading new messages. Start at one page, and if current page doesn't contain the last message
        in the database, continue and load another page. Need separate message for sent and received to make sure we've
        stopped when we reach the newest message in the database.
         */
        val allNewMessages = mutableListOf<Message>()

        val inboxPair = Pair("inbox", oldestUnreadMessageName)
        val sentPair = Pair("sent", newestSentMessageName)
        /* Load unread messages in case an older saved message was changed to unread. */
        val unreadPair = Pair("unread", "")

        val wheres = arrayOf(inboxPair, sentPair, unreadPair)
        val messageRoutines = wheres.map { (where, newestMessageNameForThisWhere) ->
                GlobalScope.async {
                    val paginator = getInboxPaginator(client, where, 30)

                    for (page in paginator.iterator()) {
                        val messages = page.takeWhile { it.fullName >= newestMessageNameForThisWhere }
                        /*
                        If the size of messages is less than the size of the page, then we know that the current page must
                        have contained the newest message we have locally, so we can stop loading pages.
                         */
                        allNewMessages += messages
                        if (messages.size < page.size) break
                    }
                }
        }
        messageRoutines.awaitAll()

        val allPrivateMessages = filterToPrivateMessages(allNewMessages)
        val allMessagesAsRedditMessages = convertNetMessageToLocalMessage(account, allPrivateMessages)
        return saveMessages(allMessagesAsRedditMessages).await()
    }

    /**
     * Loads all of a user's past private messages, downloading each 'where' in its entirety.
     * @param client The client to use for retrieving messages
     * @param account The local user who these messages should belong to upon being saved.
     */
    private suspend fun loadAllPastMessages(client: RedditClient, account: RedditAccount): List<Long> {
        Log.d("Data Repo", "Loading all past messages.")
        val wheres = arrayOf("inbox", "sent")
        val allLoadedMessages = mutableListOf<Message>()

        val messageRequests = wheres.map { where ->
            GlobalScope.async(Dispatchers.IO) {
                val messages = getInboxPaginator(client, where, 1000).accumulateMerged(-1)
                allLoadedMessages += messages
            }
        }

        messageRequests.awaitAll()
        val allPrivateMessages = filterToPrivateMessages(allLoadedMessages)
        val messagesAsLocalMessages = convertNetMessageToLocalMessage(account, allPrivateMessages)
        return saveMessages(messagesAsLocalMessages).await()
    }

    /**
     * Converts a JRAW [Message] to a local [RedditMessage], generating a unique UUID as well.
     * @param account The account who these messages will belong to. [RedditMessage.owner]
     * @param messages A list of messages to be converted.
     */
    private fun convertNetMessageToLocalMessage(account: RedditAccount, messages: List<Message>) = messages.map { message ->
        val parentId = message.firstMessage?: message.fullName

        return@map RedditMessage(account.name, message.author!!, message.dest,
            message.isUnread, message.fullName, parentId, message.created, message.subject, message.body, message.distinguished)
    }

    /**
     * Filters a list of messages to only include private messages. Reddit API classifies a private message as a message
     * whose fullname starts with 't4'
     *
     * @param messages A list of messages from JRAW to filter.
     */
    private fun filterToPrivateMessages(messages: List<Message>) = messages.filter { message -> message.fullName.startsWith("t4") }

    private fun getInboxPaginator(client: RedditClient, where: String, limit: Int) = client
        .me()
        .inbox()
        .iterate(where)
        .limit(limit)
        .build()

    fun getInboxFromClientAndAccount(user: LiveData<RedditClientAccountPair>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            appDatabase.messages().getConversationPreviews(it.account?.name)
        }
    }

    fun getMessagesFromClientAndAccount(filterOption: MessageFilterOption?, user: LiveData<RedditClientAccountPair>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            val username = it.account?.name
            when (filterOption) {
                MessageFilterOption.INBOX -> appDatabase.messages().getConversationPreviews(username)
                MessageFilterOption.SENT -> appDatabase.messages().getUserSentMessagesDesc(username)
                MessageFilterOption.UNREAD -> appDatabase.messages().getConversationsWithUnreadMessages(username)
                else -> {
                    appDatabase.messages().getConversationPreviews(username)
                }
            }
        }
    }

    fun getMessagesFromClientAndAccountPaging(filterOption: MessageFilterOption?,
                                              user: LiveData<RedditClientAccountPair>): LiveData<PagedList<RedditMessage>> {
        return Transformations.switchMap(user) {
            val username = it.account?.name
            val sourceDataSource = when (filterOption) {
                MessageFilterOption.INBOX -> appDatabase.messages().getConversationPreviewDataSource(username)
                MessageFilterOption.SENT -> appDatabase.messages().getUserSentMessagesDescDataSource(username)
                MessageFilterOption.UNREAD -> appDatabase.messages().getConversationsWithUnreadMessagesDataSource(username)
                else -> appDatabase.messages().getConversationPreviewDataSource(username)
            }

            return@switchMap LivePagedListBuilder<Int, RedditMessage>(sourceDataSource, 15).build()
        }
    }

    fun getConversationMessages(conversationParentName: String): LiveData<PagedList<RedditMessage>> {
        return LivePagedListBuilder(
            appDatabase.messages().getConversationMessages(conversationParentName), 15
        ).build()
    }

    fun getConversationMessagesWithoutPaging(conversationParentName: String): LiveData<List<RedditMessage>> {
        return appDatabase.messages().getConversationMessagesWithoutPaging(conversationParentName)
    }

    /**
     * Returns the oldest message for a conversation.
     * @param conversationParentName The first_message_name to find the oldest message for.
     */
    fun getFirstMessageOfConversation(conversationParentName: String?) = appDatabase.messages()
        .getFirstMessageOfConversation(conversationParentName)
}
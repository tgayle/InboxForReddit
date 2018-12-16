package app.tgayle.inboxforreddit.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.model.MessageFilterOption
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.network.RedditApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Message
import net.dean.jraw.oauth.AccountHelper
import java.util.*


class DataRepository(private val appDatabase: AppDatabase,
                     private val accountHelper: AccountHelper,
                     private val redditApiService: RedditApiService) {

    fun getUsers() = appDatabase.accounts().getAllUsers()
    fun getUsersDeferred() = GlobalScope.async(Dispatchers.IO ) {
        return@async appDatabase.accounts().getAllSync()
    }
    fun saveUser(user: RedditClient) = GlobalScope.async {
            val account = user.me().query().account
            if (account != null) {
                appDatabase.accounts().saveUser(RedditAccount(account.uniqueId, account.name, account.created, user.authManager.refreshToken!!))
                return@async appDatabase.accounts().getUserSync(account.name)
            } else {
                throw RuntimeException("Tried to save a user but account was null: ${user.requireAuthenticatedUser()}")
            }
        }

    fun getClientFromUser(name: String) = GlobalScope.async {
        Pair(accountHelper.switchToUser(name), appDatabase.accounts().getUserSync(name))
    }

    fun getClientFromUser(user: RedditAccount) = getClientFromUser(user.name)

    private fun saveMessages(messages: List<RedditMessage>)= GlobalScope.async {
        val result = appDatabase.messages().upsert(messages)
        Log.d("Data Repo", "Just saved ${result.size} messages.")
        return@async result
    }

    fun getMessages(user: LiveData<Pair<RedditClient, RedditAccount>>) = Transformations.switchMap(user) {
        appDatabase.messages().getUserMessages(it.second.name)
    }

    fun getInbox(user: LiveData<RedditAccount>) = Transformations.switchMap(user) {
        appDatabase.messages().getConversationPreviews(it.name)
    }

    suspend fun refreshMessages(client: RedditClient, account: RedditAccount): List<Long> {
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
        val wheres = arrayOf(inboxPair, sentPair)
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

    private fun convertNetMessageToLocalMessage(account: RedditAccount, messages: List<Message>) = messages.map { message ->
        val parentId = message.firstMessage?: message.fullName

        return@map RedditMessage(UUID.randomUUID(), account.name, message.author!!, message.dest,
            message.isUnread, message.fullName, parentId, message.created, message.subject, message.body, message.distinguished)
    }

    private fun filterToPrivateMessages(messages: List<Message>) = messages.filter { message ->
        message.fullName.startsWith("t4") // only private messages
    }

    private fun getInboxPaginator(client: RedditClient, where: String, limit: Int) = client
        .me()
        .inbox()
        .iterate(where)
        .limit(limit)
        .build()

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            appDatabase.messages().getConversationPreviews(it.second.name)
        }
    }

    fun getMessagesFromClientAndAccount(filterOption: MessageFilterOption?, user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            val username = it.second.name
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

    fun getMessagesFromClientAndAccountPaging(filterOption: MessageFilterOption?, user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<PagedList<RedditMessage>> {
        return Transformations.switchMap(user) {
            val username = it.second.name
            val sourceDataSource = when (filterOption) {
                MessageFilterOption.INBOX -> appDatabase.messages().getConversationPreviewDataSource(username)
                MessageFilterOption.SENT -> appDatabase.messages().getUserSentMessagesDescDataSource(username)
                MessageFilterOption.UNREAD -> appDatabase.messages().getConversationsWithUnreadMessagesDataSource(username)
                else -> {
                    appDatabase.messages().getConversationPreviewDataSource(username)
                }
            }

            return@switchMap LivePagedListBuilder<Int, RedditMessage>(sourceDataSource, 15).build()
        }
    }
}
package app.tgayle.inboxforreddit.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    fun saveMessages(messages: List<RedditMessage>)= GlobalScope.async {
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

    fun refreshMessages(client: RedditClient, account: RedditAccount) = GlobalScope.async {
        Log.d("DataRepo", "Starting load...")
        client.autoRenew = true

        val numPreexistingUserMessages = appDatabase.messages().getUserMessageCount(account.name)
        Log.d("Data Repo", "Message # for ${account.name} is $numPreexistingUserMessages")

        return@async if (numPreexistingUserMessages == 0) {
            loadAllPastMessages(client, account).await()
        } else {
            loadNewestMessages(client, account).await()
        }
    }

    private fun loadNewestMessages(client: RedditClient, account: RedditAccount) = GlobalScope.async {
        Log.d("Data Repo", "Attempting to load newest messages...")
        val newestSentMessage = appDatabase.messages().getNewestSentUserMessageSync(account.name)
        /*
        Get oldest unread message to make sure we update messages that are unread. This is necessary since JRAW doesn't
        currently support getting a specific message or adding a before/after query, so just load more messages to make
        sure older messages are updated.
         */
        val oldestUnreadMessage = appDatabase.messages().getOldestUnreadMessageSync(account.name)
        /*
        Be lazy about loading new messages. Start at one page, and if current page doesn't contain the last message
        in the database, continue and load another page. Need separate message for sent and received to make sure we've
        stopped when we reach the newest message in the database.
         */
        val allNewMessages = mutableListOf<Message>()
        val wheres = arrayOf(Pair("inbox", oldestUnreadMessage), Pair("sent", newestSentMessage))
        val messageRoutines = wheres.map { (where, newestMessageForThisWhere) ->
            GlobalScope.async (Dispatchers.IO) {
                val paginator = getInboxPaginator(client, where, 30)

                for (page in paginator.iterator()) {
                    val messages = page.takeWhile { it.fullName > newestMessageForThisWhere.fullName }
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
        return@async saveMessages(allMessagesAsRedditMessages).await()
    }

    private fun loadAllPastMessages(client: RedditClient, account: RedditAccount) = GlobalScope.async {
        Log.d("Data Repo", "Loading all past messages.")
        val wheres = arrayOf("inbox", "sent")
        val allLoadedMessages = mutableListOf<Message>()

        val messageRequests = wheres.map {where ->
            GlobalScope.async(Dispatchers.IO) {
                val messages = getInboxPaginator(client, where, 1000).accumulateMerged(-1)
                allLoadedMessages += messages
            }
        }
        messageRequests.awaitAll()

        val allPrivateMessages = filterToPrivateMessages(allLoadedMessages)
        val messagesAsLocalMessages = convertNetMessageToLocalMessage(account, allPrivateMessages)
        return@async saveMessages(messagesAsLocalMessages).await()
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

}
package app.tgayle.inboxforreddit.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.network.RedditApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Message
import net.dean.jraw.oauth.AccountHelper
import java.util.*


class DataRepository(private val appDatabase: AppDatabase,
                     private val accountHelper: AccountHelper,
                     private val redditApiService: RedditApiService) {

    fun getUsers() = appDatabase.accounts().getAllUsers()
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
        if (numPreexistingUserMessages == 0) return@async loadAllPastMessages(client, account).await()
        Log.d("Data Repo", "Attempting to load newest messages...")
        val newestSentMessage = appDatabase.messages().getNewestSentUserMessageSync(account.name)
        val newestReceivedMessage = appDatabase.messages().getNewestReceivedUserMessageSync(account.name)
        /*
        Be lazy about loading new messages. Start at one page, and if current page doesn't contain the last message
        in the database, continue and load another page. Need separate message for sent and received to make sure we've
        stopped when we reach the newest message in the database,
         */
        val allNewMessages = mutableListOf<Message>()
        val wheres = arrayOf(Pair("inbox", newestReceivedMessage), Pair("sent", newestSentMessage))
        val messageRoutines = wheres.map { (where, newestMessageForThisWhere) ->
            GlobalScope.async (Dispatchers.IO) {
                val paginator = client
                    .me()
                    .inbox()
                    .iterate(where)
                    .limit(30)
                    .build()

                for (page in paginator.iterator()) {
                    val messages = page.takeWhile { it.fullName < newestMessageForThisWhere.fullName }
                    /*
                    If the size of messages is less than the size of the page, then we know that the current page must
                    have contained the newest message we have locally, so we can stop loading pages.
                     */
                    if (messages.size < page.size) break
                    allNewMessages += messages
                }
            }
        }
        messageRoutines.forEach { it.await() }

        val allPrivateMessages = allNewMessages.filter { it.fullName.startsWith("t4") }
        val allMessagesAsRedditMessages = allPrivateMessages.map {
            RedditMessage(UUID.randomUUID(), account.name, it.author!!, it.dest, it.isUnread, it.fullName,
                it.firstMessage?: it.fullName, it.created, it.body, it.distinguished)
        }

        return@async saveMessages(allMessagesAsRedditMessages)
    }

    private fun loadAllPastMessages(client: RedditClient, account: RedditAccount) = GlobalScope.async {
        Log.d("Data Repo", "Loading all past messages.")
        val wheres = arrayOf("inbox", "sent")
        val allLoadedMessages = mutableListOf<Message>()

        val messageRequests = wheres.map {where ->
            GlobalScope.async(Dispatchers.IO) {
                val messages = client
                    .me()
                    .inbox()
                    .iterate(where)
                    .limit(1000).build()
                    .accumulateMerged(-1)
                allLoadedMessages += messages
            }
        }
        messageRequests.forEach { it.await() }

        val allPrivateMessages = allLoadedMessages.filter { it.fullName.startsWith("t4") } // only private messages

        return@async saveMessages(allPrivateMessages.map { message ->
            val parentId = message.firstMessage?: message.fullName

            return@map RedditMessage(UUID.randomUUID(), account.name, message.author!!, message.dest,
                message.isUnread, message.fullName, parentId, message.created, message.body, message.distinguished)

        })
            .await()
    }

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            appDatabase.messages().getConversationPreviews(it.second.name)
        }
    }

}
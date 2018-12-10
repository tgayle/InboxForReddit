package app.tgayle.inboxforreddit.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.network.RedditApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dean.jraw.RedditClient
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
        appDatabase.messages().upsert(messages)
    }

    fun getMessages(user: LiveData<Pair<RedditClient, RedditAccount>>) = Transformations.switchMap(user) {
        appDatabase.messages().getUserMessages(it.second.name)
    }

    fun getInbox(user: LiveData<RedditAccount>) = Transformations.switchMap(user) {
        appDatabase.messages().getConversationPreviews(it)
    }

    fun refreshMessages(client: RedditClient, account: RedditAccount) = GlobalScope.async {
        Log.d("DataRepo", "Starting load...")
        client.autoRenew = true
        val redditMessages = client
            .me()
            .inbox()
            .iterate("messages")
            .limit(1000).build()
            .accumulateMerged(500)
        Log.d("DataRepo", "${redditMessages.size} received!")

        return@async saveMessages(redditMessages.map {
            RedditMessage(UUID.randomUUID(), account.name, it.author!!, it.dest, it.isUnread, it.fullName,it.created, it.body, it.distinguished)
        }).await()
    }

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(user) {
            appDatabase.messages().getConversationPreviews(it.second)
        }
    }

}
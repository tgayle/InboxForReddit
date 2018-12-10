package app.tgayle.inboxforreddit.db.repository

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

    fun getInbox(user: LiveData<RedditAccount>) = Transformations.switchMap(user) {
        appDatabase.messages().getConversationPreviews(it)
    }

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>) = Transformations.switchMap(user) {
        appDatabase.messages().getConversationPreviews(it.second)
    }

}
package app.tgayle.inboxforreddit.db.repository

import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.network.RedditApiService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dean.jraw.RedditClient
import net.dean.jraw.oauth.AccountHelper

class DataRepository(private val appDatabase: AppDatabase,
                     private val accountHelper: AccountHelper,
                     private val redditApiService: RedditApiService) {

    fun getUsers() = appDatabase.dao().getAllUsers()
    fun saveUser(user: RedditClient): Deferred<Long?>  {
        return GlobalScope.async {
            val account = user.me().query().account
            if (account != null) {
                appDatabase.dao().saveUser(RedditAccount(account.uniqueId, account.name, account.created, user.authManager.refreshToken!!))
            } else {
                throw RuntimeException("Tried to save a user but account was null: ${user.requireAuthenticatedUser()}")
            }
        }
    }

    fun getClientFromUser(name: String) = accountHelper.switchToUser(name)
    fun getClientFromUser(user: RedditAccount) = getClientFromUser(user.name)

}
package app.tgayle.inboxforreddit.db.repository

import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.network.RedditApiService
import net.dean.jraw.oauth.AccountHelper
import javax.inject.Inject

class DataRepository @Inject constructor(val appDatabase: AppDatabase,
                                         val accountHelper: AccountHelper,
                                         val redditApiService: RedditApiService) {

    fun getUsers() = appDatabase.dao().getAllUsers()

}
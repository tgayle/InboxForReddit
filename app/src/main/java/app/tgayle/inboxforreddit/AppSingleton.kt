package app.tgayle.inboxforreddit

import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.di.components.InboxApplicationComponent
import app.tgayle.inboxforreddit.network.RedditApiService
import net.dean.jraw.oauth.AccountHelper

object AppSingleton {
    lateinit var redditApiService: RedditApiService
    lateinit var appDatabase: AppDatabase
    lateinit var redditHelper: AccountHelper
    lateinit var dataRepository: DataRepository
    fun setup(inboxApplicationComponent: InboxApplicationComponent) {
        dataRepository = inboxApplicationComponent.dataRepository()
        redditApiService = inboxApplicationComponent.redditApiService()
        appDatabase = inboxApplicationComponent.database()
        redditHelper = inboxApplicationComponent.redditHelper()
    }
}
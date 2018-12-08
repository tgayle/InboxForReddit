package app.tgayle.inboxforreddit.di.components

import app.tgayle.inboxforreddit.InboxApplication
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.di.modules.ActivityInjector
import app.tgayle.inboxforreddit.di.modules.DatabaseModule
import app.tgayle.inboxforreddit.di.modules.NetworkModule
import app.tgayle.inboxforreddit.network.RedditApiService
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.dean.jraw.oauth.AccountHelper
import retrofit2.Retrofit

@Component(modules = [NetworkModule::class, DatabaseModule::class, AndroidInjectionModule::class, ActivityInjector::class])
@InboxApplicationScope
interface InboxApplicationComponent {
    fun retrofit(): Retrofit
    fun database(): AppDatabase
    fun redditApiService(): RedditApiService
    fun redditHelper(): AccountHelper
    fun inject(application: InboxApplication)
}
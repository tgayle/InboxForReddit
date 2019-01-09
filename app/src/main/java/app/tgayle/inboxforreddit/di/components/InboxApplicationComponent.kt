package app.tgayle.inboxforreddit.di.components

import app.tgayle.inboxforreddit.InboxApplication
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.di.modules.DatabaseModule
import app.tgayle.inboxforreddit.di.modules.NetworkModule
import app.tgayle.inboxforreddit.di.modules.RedditModule
import app.tgayle.inboxforreddit.di.modules.android.ActivityBuilder
import app.tgayle.inboxforreddit.network.RedditApiService
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.dean.jraw.oauth.AccountHelper
import retrofit2.Retrofit

@Component(
    modules = [NetworkModule::class, DatabaseModule::class, RedditModule::class,
                AndroidInjectionModule::class, ActivityBuilder::class]
)
@InboxApplicationScope
interface InboxApplicationComponent {
    fun retrofit(): Retrofit
    fun database(): AppDatabase
    fun dataRepository(): DataRepository
    fun redditApiService(): RedditApiService
    fun redditHelper(): AccountHelper
    fun inject(application: InboxApplication)
}
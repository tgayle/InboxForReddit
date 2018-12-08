package app.tgayle.inboxforreddit.di.components

import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.di.modules.DatabaseModule
import app.tgayle.inboxforreddit.di.modules.NetworkModule
import dagger.Component
import retrofit2.Retrofit

@Component(modules = [NetworkModule::class, DatabaseModule::class])
@InboxApplicationScope
interface InboxApplicationComponent {
    fun retrofit(): Retrofit
    fun database(): AppDatabase
}
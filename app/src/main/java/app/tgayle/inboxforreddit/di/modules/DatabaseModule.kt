package app.tgayle.inboxforreddit.di.modules

import android.content.Context
import androidx.room.Room
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.network.RedditApiService
import dagger.Module
import dagger.Provides
import net.dean.jraw.oauth.AccountHelper

@Module(includes = [ContextModule::class])
class DatabaseModule {
    @InboxApplicationScope
    @Provides
    fun room(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "inbox_database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries() // Allow main thread to populate JRAW token store
            .build()
    }

    @InboxApplicationScope
    @Provides
    fun dataRepository(appDatabase: AppDatabase,
                       accountHelper: AccountHelper,
                       redditApiService: RedditApiService
    ): DataRepository = DataRepository(appDatabase, accountHelper, redditApiService)
}
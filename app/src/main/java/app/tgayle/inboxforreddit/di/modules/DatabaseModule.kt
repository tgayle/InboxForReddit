package app.tgayle.inboxforreddit.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import dagger.Module
import dagger.Provides

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
    fun sharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("inbox_preferences", Context.MODE_PRIVATE)
    }
}
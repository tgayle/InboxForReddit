package app.tgayle.inboxforreddit.di.modules

import android.content.Context
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.db.RoomTokenStore
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import dagger.Module
import dagger.Provides
import net.dean.jraw.android.AndroidHelper
import net.dean.jraw.android.ManifestAppInfoProvider
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import okhttp3.OkHttpClient
import java.util.*


@Module(includes = [DatabaseModule::class, ContextModule::class])
class RedditModule {
    @Provides
    @InboxApplicationScope
    fun appInfoProvider(appContext: Context) = ManifestAppInfoProvider(appContext)

    @Provides
    @InboxApplicationScope
    fun accountHelper(appInfoProvider: ManifestAppInfoProvider, uuid: UUID, tokenStore: RoomTokenStore) = AndroidHelper.accountHelper(appInfoProvider, uuid, tokenStore)

    @Provides
    @InboxApplicationScope
    fun tokenStore(appDatabase: AppDatabase) = RoomTokenStore(appDatabase)

    @Provides
    @InboxApplicationScope
    fun uuid() = UUID.randomUUID()

    @Provides
    @InboxApplicationScope
    fun okHttpNetworkAdapter(userAgent: UserAgent, okHttpClient: OkHttpClient) = OkHttpNetworkAdapter(userAgent, okHttpClient)
}
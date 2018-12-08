package app.tgayle.inboxforreddit.di.modules

import app.tgayle.inboxforreddit.di.InboxApplicationScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    @Provides
    @InboxApplicationScope
    fun retrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl("https://oauth.reddit.com")
        .addConverterFactory(gsonConverterFactory)
        .client(client)
        .build()

    @Provides
    @InboxApplicationScope
    fun gsonConverterFactory() = GsonConverterFactory.create()

    @Provides
    @InboxApplicationScope
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

}
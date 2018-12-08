package app.tgayle.inboxforreddit.di.modules

import android.content.Context
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {
    @Provides
    @InboxApplicationScope
    fun context() = context
}
package app.tgayle.inboxforreddit

import android.app.Application
import app.tgayle.inboxforreddit.di.components.DaggerInboxApplicationComponent
import app.tgayle.inboxforreddit.di.modules.ContextModule

class InboxApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val appComponent = DaggerInboxApplicationComponent.builder().contextModule(ContextModule(this)).build()

    }
}
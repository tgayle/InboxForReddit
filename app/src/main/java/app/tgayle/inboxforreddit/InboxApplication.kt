package app.tgayle.inboxforreddit

import android.app.Application
import app.tgayle.inboxforreddit.di.components.DaggerInboxApplicationComponent
import app.tgayle.inboxforreddit.di.components.InboxApplicationComponent
import app.tgayle.inboxforreddit.di.modules.ContextModule
import javax.inject.Inject

class InboxApplication: Application() {
    @Inject
    lateinit var appComponent: InboxApplicationComponent

    override fun onCreate() {
        DaggerInboxApplicationComponent.builder()
            .contextModule(ContextModule(this))
            .build()
            .inject(this)

        AppSingleton.setup(appComponent)
        super.onCreate()

    }

}
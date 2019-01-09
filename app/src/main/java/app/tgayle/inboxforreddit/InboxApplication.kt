package app.tgayle.inboxforreddit

import android.app.Activity
import android.app.Application
import app.tgayle.inboxforreddit.di.components.DaggerInboxApplicationComponent
import app.tgayle.inboxforreddit.di.components.InboxApplicationComponent
import app.tgayle.inboxforreddit.di.modules.ContextModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject



class InboxApplication: Application(), HasActivityInjector {
    @Inject lateinit var appComponent: InboxApplicationComponent
    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        DaggerInboxApplicationComponent.builder()
            .contextModule(ContextModule(this))
            .build()
            .inject(this)

        AppSingleton.setup(appComponent)
        super.onCreate()

    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

}
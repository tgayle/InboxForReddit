package app.tgayle.inboxforreddit.di.modules.android

import app.tgayle.inboxforreddit.screens.mainactivity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}
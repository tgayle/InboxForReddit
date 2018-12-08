package app.tgayle.inboxforreddit.di.modules

import app.tgayle.inboxforreddit.screens.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityInjector {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}
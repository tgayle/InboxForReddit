package app.tgayle.inboxforreddit.di.modules.android

import app.tgayle.inboxforreddit.screens.homescreen.HomeFragment
import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.ConversationFragment
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.InboxFragment
import app.tgayle.inboxforreddit.screens.homescreen.replyscreen.ReplyConversationFragment
import app.tgayle.inboxforreddit.screens.loginscreen.LoginScreenFragment
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragment
import app.tgayle.inboxforreddit.screens.splashscreen.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeInboxFragment(): InboxFragment

    @ContributesAndroidInjector
    abstract fun contributeConversationFragment(): ConversationFragment

    @ContributesAndroidInjector
    abstract fun contributeReplyConversationFragment(): ReplyConversationFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeBottomNavigationDrawerFragment(): BottomNavigationDrawerFragment
}
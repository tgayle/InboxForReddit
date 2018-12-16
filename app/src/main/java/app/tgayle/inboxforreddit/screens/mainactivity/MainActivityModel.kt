package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import app.tgayle.inboxforreddit.model.RedditAccount
import net.dean.jraw.RedditClient

interface MainActivityModel {
    interface Listener {
    }

    fun onIntentOccurred(intent: Intent)
    fun onRedditClientUpdated(client: Pair<RedditClient, RedditAccount>)
    fun requestToolbarTitleChange(title: String?)
    fun requestChangeToolbarScrollState(enabled: Boolean)
    fun requestAccountSwitch(username: String?)
    fun requestNavigateAddAccount()
}
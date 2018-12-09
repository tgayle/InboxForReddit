package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import net.dean.jraw.RedditClient

interface MainActivityModel {
    interface Listener {
    }

    fun onIntentOccurred(intent: Intent)
    fun onRedditClientUpdated(client: RedditClient)
}
package app.tgayle.inboxforreddit.screens.splashscreen

import androidx.lifecycle.LiveData
import app.tgayle.inboxforreddit.model.RedditAccount

interface SplashScreenModel {
    interface Listener {
        fun listenForNavigation()
        fun listenForActionDispatch()
    }

    fun getAllUsers(): LiveData<List<RedditAccount>?>
}
package app.tgayle.inboxforreddit.screens.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.tgayle.inboxforreddit.model.RedditAccount

interface SplashScreenModel {
    val navigationDecision: MutableLiveData<Int?>

    interface Listener {
        fun listenForNavigation()
        fun listenForUsers()
    }

    fun getAllUsers(): LiveData<List<RedditAccount>?>
    fun onUsersUpdate(users: List<RedditAccount>?)
}
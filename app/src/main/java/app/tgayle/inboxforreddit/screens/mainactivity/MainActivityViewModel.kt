package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.util.default
import net.dean.jraw.RedditClient

class MainActivityViewModel(dataRepository: DataRepository): ViewModel(), MainActivityModel {
    private val redditClient = MutableLiveData<Pair<RedditClient, RedditAccount>>()
    private val currentToolbarText = MutableLiveData<String>()
    private val toolbarScrollEnabled = MutableLiveData<Boolean>().default(true)

    override fun onIntentOccurred(intent: Intent) {
    }

    override fun onRedditClientUpdated(client: Pair<RedditClient, RedditAccount>) {
        Log.d("MainActivityVM", "Client set to $client")
        redditClient.value = client
    }

    fun getRedditClient(): LiveData<Pair<RedditClient, RedditAccount>> = redditClient
    fun getCurrentToolbarText(): LiveData<String> = currentToolbarText
    fun getToolbarScrollEnabled(): LiveData<Boolean> = toolbarScrollEnabled

    override fun requestToolbarTitleChange(title: String?) {
        if (title != null) {
            currentToolbarText.value = title
        }
    }

    override fun requestChangeToolbarScrollState(enabled: Boolean) {
        toolbarScrollEnabled.value = enabled
        Log.d("MainActivityViewModel", "Toolbar scroll to be set to $enabled")
    }
}
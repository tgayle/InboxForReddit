package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import net.dean.jraw.RedditClient

class MainActivityViewModel(dataRepository: DataRepository): ViewModel(), MainActivityModel {
    private val redditClient = MutableLiveData<Pair<RedditClient, RedditAccount>>()
    private val currentToolbarText = MutableLiveData<String>()

    override fun onIntentOccurred(intent: Intent) {
    }

    override fun onRedditClientUpdated(client: Pair<RedditClient, RedditAccount>) {
        Log.d("MainActivityVM", "Client set to $client")
        redditClient.value = client
    }

    fun getRedditClient(): LiveData<Pair<RedditClient, RedditAccount>> = redditClient
    fun getCurrentToolbarText(): LiveData<String> = currentToolbarText

    override fun requestToolbarTitleChange(title: String?) {
        if (title != null) {
            currentToolbarText.value = title
        }
    }
}
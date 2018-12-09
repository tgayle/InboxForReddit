package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import net.dean.jraw.RedditClient

class MainActivityViewModel(dataRepository: DataRepository): ViewModel(), MainActivityModel {
    val redditClient = MutableLiveData<RedditClient>()

    override fun onIntentOccurred(intent: Intent) {
    }

    override fun onRedditClientUpdated(client: RedditClient) {
        Log.d("MainActivityVM", "Client set to $client")
        redditClient.value = client
    }

}
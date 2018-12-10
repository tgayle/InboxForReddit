package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dean.jraw.RedditClient

class InboxFragmentViewModel(val dataRepository: DataRepository) : ViewModel(), InboxScreenModel {
    private val refreshing = MutableLiveData<Boolean>()

    override fun getInbox(user: LiveData<RedditAccount>) = dataRepository.getInbox(user)
    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>) = dataRepository.getInboxFromClientAndAccount(user)
    fun getUserMessages(user: LiveData<Pair<RedditClient, RedditAccount>>) = dataRepository.getMessages(user)
    fun getRefreshing(): LiveData<Boolean> = refreshing

    override fun onRefresh(user: Pair<RedditClient, RedditAccount>?) {
        if (user == null) return
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("Inbox", "Send refresh request.")
            refreshing.value = true
            dataRepository.refreshMessages(user.first, user.second).await()
            refreshing.value = false
        }
    }
}

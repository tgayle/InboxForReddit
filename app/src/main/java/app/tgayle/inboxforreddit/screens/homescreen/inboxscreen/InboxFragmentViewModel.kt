package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.MessageFilterOption
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.util.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dean.jraw.RedditClient

class InboxFragmentViewModel(val dataRepository: DataRepository) : ViewModel(), InboxScreenModel {
    private val isRefreshing = MutableLiveData<Boolean>()
    private val currentMessageFilter = MutableLiveData<MessageFilterOption>().default(
        MessageFilterOption.INBOX)

    override fun getInbox(user: LiveData<RedditAccount>): LiveData<List<RedditMessage>> = dataRepository.getInbox(user)
    fun getUserMessages(user: LiveData<Pair<RedditClient, RedditAccount>>) = dataRepository.getMessages(user)
    fun getRefreshing(): LiveData<Boolean> = isRefreshing

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<List<RedditMessage>> {
        return Transformations.switchMap(currentMessageFilter) {
            dataRepository.getMessagesFromClientAndAccount(it, user)
        }
    }

    fun getCurrentFilterTitle() = Transformations.map(currentMessageFilter) { it.name.toLowerCase().capitalize() }

    override fun onRefresh(user: Pair<RedditClient, RedditAccount>?) {
        if (user == null) return
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("Inbox", "Send refresh request.")
            isRefreshing.value = true
            dataRepository.refreshMessages(user.first, user.second).await()
            isRefreshing.value = false
        }
    }

    override fun onFilterSelection(item: Int?): Boolean {
        when (item) {
            R.id.menu_filter_inbox -> {
                currentMessageFilter.value = MessageFilterOption.INBOX
                return true
            }
            R.id.menu_filter_unread -> {
                currentMessageFilter.value = MessageFilterOption.UNREAD
                return true
            }
            R.id.menu_filter_sent -> {
                currentMessageFilter.value = MessageFilterOption.SENT
                return true
            }
            else -> {
                currentMessageFilter.value = MessageFilterOption.INBOX
            }
        }
        return false
    }
}
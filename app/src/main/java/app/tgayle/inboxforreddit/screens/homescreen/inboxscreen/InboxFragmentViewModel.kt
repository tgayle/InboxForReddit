package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.MessageFilterOption
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.util.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dean.jraw.RedditClient
import java.util.*
import java.util.concurrent.TimeUnit

class InboxFragmentViewModel(val dataRepository: DataRepository) : ViewModel(), InboxScreenModel {
    private val isRefreshing = MutableLiveData<Boolean>()
    private val currentMessageFilter = MutableLiveData<MessageFilterOption>().default(
        MessageFilterOption.INBOX)
    private var lastRefresh: Date? = null
    private val timeBetweenAutomaticRefresh = TimeUnit.SECONDS.toMillis(30)

    override fun getInbox(user: LiveData<RedditAccount>): LiveData<List<RedditMessage>> = dataRepository.getInbox(user)
    fun getUserMessages(user: LiveData<Pair<RedditClient, RedditAccount>>) = dataRepository.getMessages(user)
    fun getRefreshing(): LiveData<Boolean> = isRefreshing

    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>): LiveData<PagedList<RedditMessage>> {
        return Transformations.switchMap(currentMessageFilter) {
            dataRepository.getMessagesFromClientAndAccountPaging(it, user)
        }
    }

    fun getCurrentFilterTitle() = Transformations.map(currentMessageFilter) { it.name.toLowerCase().capitalize() }

    override fun onRefresh(user: Pair<RedditClient, RedditAccount>?, wasUserInteractionInvolved: Boolean) {
        if (user == null) return
        val currentTime = Date()

        // Don't refresh if a refresh was requested because of an onResume or something that wasn't a direct result of
        // user interaction.
        if (!wasUserInteractionInvolved) {
            lastRefresh.let {
                if (it != null) {
                    val tenMinutesPassed = it.time + timeBetweenAutomaticRefresh < currentTime.time
                    if (!tenMinutesPassed) return
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            Log.d("Inbox", "Send refresh request.")
            isRefreshing.value = true
            withContext(Dispatchers.Default) {
                dataRepository.refreshMessages(user.first, user.second)
            }
            isRefreshing.value = false
            lastRefresh = currentTime
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

    fun shouldRequestPreventToolbarScroll(lastItemVisible: Boolean): Boolean {
        return !lastItemVisible
    }
}
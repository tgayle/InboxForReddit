package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.model.MessageFilterOption
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditClientAccountPair
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.util.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class InboxFragmentViewModel(private val messageRepository: MessageRepository,
                             userRepository: UserRepository) : ViewModel() {
    var inboxViewState: InboxScreenStateArgs = InboxScreenStateArgs()
    private set

    val currentUser = userRepository.getCurrentRedditUser()
    private val isRefreshing = MutableLiveData<Boolean>()
    private val currentMessageFilter = MutableLiveData<MessageFilterOption>().default(MessageFilterOption.INBOX)
    private val viewState = MutableLiveData<InboxFragmentState?>()

    private var lastRefresh: Date? = null
    private val timeBetweenAutomaticRefresh = TimeUnit.SECONDS.toMillis(30)

    fun getInbox(user: LiveData<RedditAccount>): LiveData<List<RedditMessage>> = messageRepository.getInbox(user)
    fun getUserMessages(user: LiveData<RedditClientAccountPair>) = messageRepository.getMessages(user)
    fun getRefreshing(): LiveData<Boolean> = isRefreshing
    fun getState(): LiveData<InboxFragmentState?> = viewState

    fun getInboxFromClientAndAccount(user: LiveData<RedditClientAccountPair>): LiveData<PagedList<RedditMessage>> {
        return Transformations.switchMap(currentMessageFilter) {
            messageRepository.getMessagesFromClientAndAccountPaging(it, user)
        }
    }

    fun getCurrentFilterTitle() = Transformations.map(currentMessageFilter) { it.name.toLowerCase().capitalize() }

    fun onMessageClicked(message: RedditMessage, position: Int) {
        viewState.value = InboxFragmentState.NavigateConversation(message, position)
    }

    fun onActionAcknowledged() {
        viewState.value = null
    }

    fun onRefresh(user: RedditClientAccountPair?, wasUserInteractionInvolved: Boolean) {
        if (user == null) return
        val currentTime = Date()

        // Don't refresh if a refresh was requested because of an onResume or something that wasn't a direct result of
        // user interaction.
        if (!wasUserInteractionInvolved) {
            lastRefresh.let {
                if (it != null) {
                    val thirtySecondsAgo = currentTime.time - timeBetweenAutomaticRefresh
                    if (it.time > thirtySecondsAgo) return
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            Log.d("Inbox", "Send refresh request.")
            isRefreshing.value = true
            withContext(Dispatchers.Default) {
                messageRepository.refreshMessages(user.client, user.account!!)
            }
            isRefreshing.value = false
            lastRefresh = currentTime
        }
    }

    fun onFilterSelection(item: Int?): Boolean {
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

    fun onFragmentStop(state: InboxScreenStateArgs) {
        inboxViewState = state.copy(lastAccount = currentUser.value)
    }

}
package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import net.dean.jraw.RedditClient

class InboxFragmentViewModel(val dataRepository: DataRepository) : ViewModel() {

    fun getInbox(user: LiveData<RedditAccount>) = dataRepository.getInbox(user)
    fun getInboxFromClientAndAccount(user: LiveData<Pair<RedditClient, RedditAccount>>) = dataRepository.getInboxFromClientAndAccount(user)
}

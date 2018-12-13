package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.LiveData
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import net.dean.jraw.RedditClient

interface InboxScreenModel {
    interface Listener {
        fun onRefresh()
    }

    fun getInbox(user: LiveData<RedditAccount>): LiveData<List<RedditMessage>>?
    fun onRefresh(user: Pair<RedditClient, RedditAccount>?)
    fun onFilterSelection(item: Int?): Boolean
}
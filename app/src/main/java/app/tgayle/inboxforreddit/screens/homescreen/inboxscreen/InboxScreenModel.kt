package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.LiveData
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditClientAccountPair
import app.tgayle.inboxforreddit.model.RedditMessage

interface InboxScreenModel {
    interface Listener {
        fun onRefresh()
    }

    fun getInbox(user: LiveData<RedditAccount>): LiveData<List<RedditMessage>>?
    fun onFilterSelection(item: Int?): Boolean
    fun onRefresh(user: RedditClientAccountPair?, wasUserInteractionInvolved: Boolean)
}
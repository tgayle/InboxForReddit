package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.LiveData
import app.tgayle.inboxforreddit.model.RedditMessage

interface InboxScreenModel {
    interface Listener {

    }

    fun getInbox(): LiveData<List<RedditMessage>>
}
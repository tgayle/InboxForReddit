package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import app.tgayle.inboxforreddit.model.RedditMessage

sealed class InboxFragmentState {
    class NavigateConversation(val message: RedditMessage, val position: Int) : InboxFragmentState()
}
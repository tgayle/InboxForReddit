package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import app.tgayle.inboxforreddit.model.RedditClientAccountPair

data class InboxScreenStateArgs(var firstVisibleMessagePosition: Int = 0, var viewOffset: Int = 0, val lastAccount: RedditClientAccountPair? = null)
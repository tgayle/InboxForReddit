package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view

import app.tgayle.inboxforreddit.model.RedditMessage

data class ConversationRecyclerViewItem(val message: RedditMessage, var isCollapsed: Boolean = false)
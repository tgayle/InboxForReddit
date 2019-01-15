package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view

import app.tgayle.inboxforreddit.model.RedditMessage

/**
 * Represents the state of a message in the list of ConversationFragment's messages.
 */
data class ConversationRecyclerViewItem(val message: RedditMessage, var isCollapsed: Boolean = false)
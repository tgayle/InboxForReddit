package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view.ConversationRecyclerViewItem

sealed class ConversationFragmentState {
    class ToggleListItemCollapse(val position: Int, val newItemState: ConversationRecyclerViewItem): ConversationFragmentState()
}

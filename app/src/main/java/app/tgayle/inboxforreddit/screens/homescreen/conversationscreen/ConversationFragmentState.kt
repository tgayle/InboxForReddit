package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view.ConversationRecyclerViewItem

/**
 * A sealed class representing the state of [ConversationFragment]
 */
sealed class ConversationFragmentState {

    /**
     * Action to toggle the collapsed state of a message in the view.
     * @param position The position of the item to change
     * @param newItemState The new state to update the item with.
     */
    class ToggleListItemCollapse(val position: Int,
                                 val newItemState: ConversationRecyclerViewItem): ConversationFragmentState()
}

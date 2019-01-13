package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.MessageRepository

class ConversationViewModelFactory(private val conversationParentName: String, private val messageRepository: MessageRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(conversationParentName, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
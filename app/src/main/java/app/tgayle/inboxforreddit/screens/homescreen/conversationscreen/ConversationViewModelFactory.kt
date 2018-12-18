package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.DataRepository

class ConversationViewModelFactory(private val conversationParentName: String, private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(conversationParentName, dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
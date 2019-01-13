package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository

class InboxFragmentViewModelFactory(private val messageRepository: MessageRepository,
                                    private val userRepository: UserRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InboxFragmentViewModel::class.java)) {
            return InboxFragmentViewModel(messageRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
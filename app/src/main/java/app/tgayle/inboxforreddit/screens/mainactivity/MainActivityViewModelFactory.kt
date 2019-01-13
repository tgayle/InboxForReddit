package app.tgayle.inboxforreddit.screens.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository

class MainActivityViewModelFactory(private val messageRepository: MessageRepository, private val userRepository: UserRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(messageRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
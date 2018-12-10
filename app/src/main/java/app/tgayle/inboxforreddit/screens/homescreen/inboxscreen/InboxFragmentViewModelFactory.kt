package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.DataRepository

class InboxFragmentViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InboxFragmentViewModel::class.java)) {
            return InboxFragmentViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
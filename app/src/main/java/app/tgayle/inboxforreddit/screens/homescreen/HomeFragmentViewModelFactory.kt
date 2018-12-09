package app.tgayle.inboxforreddit.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.screens.splashscreen.SplashFragmentViewModel

class HomeFragmentViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashFragmentViewModel::class.java)) {
            return HomeFragmentViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
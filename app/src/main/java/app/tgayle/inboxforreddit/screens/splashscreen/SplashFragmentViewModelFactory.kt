package app.tgayle.inboxforreddit.screens.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.DataRepository


class SplashFragmentViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashFragmentViewModel::class.java)) {
            return SplashFragmentViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
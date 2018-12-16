package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.DataRepository

class BottomNavigationDrawerFragmentViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomNavigationDrawerFragmentViewModel::class.java)) {
            return BottomNavigationDrawerFragmentViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
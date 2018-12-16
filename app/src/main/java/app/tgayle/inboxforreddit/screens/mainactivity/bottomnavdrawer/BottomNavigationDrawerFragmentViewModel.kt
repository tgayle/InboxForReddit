package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository

class BottomNavigationDrawerFragmentViewModel(val dataRepository: DataRepository): ViewModel() {
    fun getUsersList() = dataRepository.getUsersAndUnreadMessageCountForEach()
}
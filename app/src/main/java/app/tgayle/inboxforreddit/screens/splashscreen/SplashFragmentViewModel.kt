package app.tgayle.inboxforreddit.screens.splashscreen

import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import net.dean.jraw.oauth.AccountHelper
import javax.inject.Inject

class SplashFragmentViewModel @Inject constructor(val dataRepository: DataRepository, val redditAccountHelper: AccountHelper): ViewModel() {
    fun getAllUsers() = dataRepository.getUsers()
}
package app.tgayle.inboxforreddit.screens.splashscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.util.SingleLiveEvent

class SplashFragmentViewModel(val dataRepository: DataRepository): ViewModel(), SplashScreenModel {
    override val navigationDecision = SingleLiveEvent<Int?>()

    override fun onUsersUpdate(users: List<RedditAccount>?) {
        if (users == null) return

        navigationDecision.value = if (users.isEmpty()) {
            Log.d("Splash", "Navigating to Login")
            R.id.action_splashFragment_to_loginFragment
        } else {
            Log.d("Splash", "Navigating to Home")
            R.id.action_splashFragment_to_homeFragment
        }
    }

    override fun getAllUsers() = dataRepository.getUsers()
}
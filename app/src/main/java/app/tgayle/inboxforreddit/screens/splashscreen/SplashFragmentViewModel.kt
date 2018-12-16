package app.tgayle.inboxforreddit.screens.splashscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashFragmentViewModel(val dataRepository: DataRepository): ViewModel(), SplashScreenModel {
    val navigationDecision = SingleLiveEvent<Int?>()

    init {
        GlobalScope.launch(Dispatchers.Main) {
            val users = dataRepository.getUsersDeferred().await()
            if (users.isEmpty()) {
                Log.d("Splash", "Navigating to Login")
                navigationDecision.value = R.id.action_splashFragment_to_loginFragment
            } else {
                dataRepository.updateCurrentUser(dataRepository.getClientFromUser(users[0]).await())
                Log.d("Splash", "Navigating to Home")
                navigationDecision.value = R.id.action_splashFragment_to_homeFragment
            }
        }
    }

    override fun getAllUsers() = dataRepository.getUsers()
}
package app.tgayle.inboxforreddit.screens.splashscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashFragmentViewModel(private val userRepository: UserRepository): ViewModel(), SplashScreenModel {
    val navigationDecision = SingleLiveEvent<Int?>()

    init {
        GlobalScope.launch(Dispatchers.Main) {
            val sharedPreferenceLastUser = userRepository.getSharedPreferencesCurrentUser()
            if (sharedPreferenceLastUser == null) {
                Log.d("Splash", "Navigating to Login")
                navigationDecision.value = R.id.action_splashFragment_to_loginFragment
            } else {
                val user = userRepository.getClientFromUser(sharedPreferenceLastUser).await()
                userRepository.updateCurrentUser(user)
                Log.d("Splash", "Navigating to Home")
                navigationDecision.value = R.id.action_splashFragment_to_homeFragment
            }
        }
    }

    override fun getAllUsers() = userRepository.getUsers()
}
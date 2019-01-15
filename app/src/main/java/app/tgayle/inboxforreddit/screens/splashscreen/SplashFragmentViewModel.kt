package app.tgayle.inboxforreddit.screens.splashscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashFragmentViewModel(private val userRepository: UserRepository): ViewModel(), SplashScreenModel {
    private val navigationDecision = MutableLiveData<SplashFragmentState>()

    init {
        GlobalScope.launch(Dispatchers.Main) {
            val sharedPreferenceLastUser = userRepository.getSharedPreferencesCurrentUser()
            if (sharedPreferenceLastUser == null) {
                Log.d("Splash", "Navigating to Login")

                navigationDecision.value = SplashFragmentState.NavigateLogin
            } else {
                val user = userRepository.getClientFromUser(sharedPreferenceLastUser).await()
                userRepository.updateCurrentUser(user)
                Log.d("Splash", "Navigating to Home")
                navigationDecision.value = SplashFragmentState.NavigateHome
            }
        }
    }

    internal fun getState(): LiveData<SplashFragmentState> = navigationDecision
    override fun getAllUsers() = userRepository.getUsers()
}
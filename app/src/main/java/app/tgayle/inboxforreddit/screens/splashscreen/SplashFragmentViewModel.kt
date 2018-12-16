package app.tgayle.inboxforreddit.screens.splashscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dean.jraw.RedditClient

class SplashFragmentViewModel(val dataRepository: DataRepository): ViewModel(), SplashScreenModel {
    val navigationDecision = SingleLiveEvent<Int?>()
    private val viewModelDispatch = MutableLiveData<SplashScreenAction?>()
    private val locatedRedditAccount = MutableLiveData<Pair<RedditClient, RedditAccount>>()

    enum class SplashScreenAction {
        UPDATE_ACTIVITY_VM_WITH_REDDIT
    }

    init {
        GlobalScope.launch(Dispatchers.Main) {
            val users = dataRepository.getUsersDeferred().await()
            if (users.isEmpty()) {
                Log.d("Splash", "Navigating to Login")
                navigationDecision.value = R.id.action_splashFragment_to_loginFragment
            } else {
                val userData = dataRepository.getClientFromUser(users[0]).await()
                locatedRedditAccount.value = if (userData.second != null) {
                     Pair(userData.first, userData.second!!)
                } else {
                    null
                }
                viewModelDispatch.value = SplashScreenAction.UPDATE_ACTIVITY_VM_WITH_REDDIT
                Log.d("Splash", "Navigating to Home")

                navigationDecision.value = R.id.action_splashFragment_to_homeFragment
            }
        }
    }

    fun getViewmodelDispatch(): LiveData<SplashScreenAction?> = viewModelDispatch
    fun getLocatedRedditAccount(): LiveData<Pair<RedditClient, RedditAccount>> = locatedRedditAccount
    override fun getAllUsers() = dataRepository.getUsers()
}
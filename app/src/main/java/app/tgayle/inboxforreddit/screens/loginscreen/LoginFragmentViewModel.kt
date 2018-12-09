package app.tgayle.inboxforreddit.screens.loginscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.db.repository.DataRepository
import kotlinx.coroutines.*
import net.dean.jraw.RedditClient

class LoginFragmentViewModel(val dataRepository: DataRepository): ViewModel(), LoginScreenModel {
    private val jrawAuthHelper = AppSingleton.redditHelper.switchToNewUser()
    val scopes = arrayOf("modmail", "modlog", "read", "privatemessages", "report", "identity")
    val loginUrl = jrawAuthHelper.getAuthorizationUrl(true, true, *scopes)
    val actionDispatch = MutableLiveData<FragmentActions>()
    val navigationDecision = MutableLiveData<LoginFragmentNavigation>()
    val redditClient =  MutableLiveData<RedditClient?>()

    override fun onLoginOccurred(link: String) {

        if (jrawAuthHelper.isFinalRedirectUrl(link)) {
            navigationDecision.value = LoginFragmentNavigation.LOADING
            GlobalScope.launch(Dispatchers.Main) {
                redditClient.value = attemptLogin(link).await()
                actionDispatch.value = FragmentActions.ALERT_MAIN_VM_WITH_REDDIT
                navigationDecision.value = LoginFragmentNavigation.HOME
            }
        }

    }

    fun attemptLogin(link: String): Deferred<RedditClient> {
            return GlobalScope.async {
                Log.d("Login", "Attempting Login")
                val redditClient = jrawAuthHelper.onUserChallenge(link)
                Log.d("Login", "Login Success!: ${redditClient.me().username}")
                redditClient
            }
    }

    enum class LoginFragmentNavigation {
        HOME,
        LOGIN,
        LOADING
    }

    enum class FragmentActions {
        ALERT_MAIN_VM_WITH_REDDIT
    }

}
package app.tgayle.inboxforreddit.screens.loginscreen

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditAccount
import kotlinx.coroutines.*
import net.dean.jraw.RedditClient

class LoginFragmentViewModel(val dataRepository: DataRepository): ViewModel(), LoginScreenModel {
    private val jrawAuthHelper = AppSingleton.redditHelper.switchToNewUser()
    private val scopes = arrayOf("modmail", "modlog", "read", "privatemessages", "report", "identity")
    val loginUrl = jrawAuthHelper.getAuthorizationUrl(true, true, *scopes)
    private val actionDispatchMutable = MutableLiveData<FragmentActions>()
    private val navigationDecisionMutable = MutableLiveData<LoginFragmentNavigation>()
    private val redditClientMutable =  MutableLiveData<Pair<RedditClient, RedditAccount>?>()

    override fun onLoginOccurred(link: String) {
        if (jrawAuthHelper.isFinalRedirectUrl(link)) {
            navigationDecisionMutable.value = LoginFragmentNavigation.LOADING
            GlobalScope.launch(Dispatchers.Main) {
                val redditClient = attemptLogin(link).await()
                val redditAccount = dataRepository.saveUser(redditClient).await()
                redditClientMutable.value = Pair(redditClient, redditAccount)


                actionDispatchMutable.value = FragmentActions.ALERT_MAIN_VM_WITH_REDDIT
                navigationDecisionMutable.value = LoginFragmentNavigation.HOME
            }
        }
    }

    override fun shouldPopBackStackOnFinish(bundleArgs: Bundle?): Boolean {
        return bundleArgs?.getBoolean("popBackStackAfterLogin") ?: false
    }

    fun attemptLogin(link: String): Deferred<RedditClient> {
            return GlobalScope.async {
                Log.d("Login", "Attempting Login")
                val redditClient = jrawAuthHelper.onUserChallenge(link)
                redditClient.authManager.refreshToken
                Log.d("Login", "Login Success!: ${redditClient.me().username}")
                redditClient
            }
    }

    fun getActionDispatch(): LiveData<FragmentActions> = actionDispatchMutable
    fun getNavigationDecision(): LiveData<LoginFragmentNavigation> = navigationDecisionMutable
    fun getRedditClient(): LiveData<Pair<RedditClient, RedditAccount>?> = redditClientMutable

    enum class LoginFragmentNavigation {
        HOME,
        LOGIN,
        LOADING
    }

    enum class FragmentActions {
        ALERT_MAIN_VM_WITH_REDDIT
    }

}
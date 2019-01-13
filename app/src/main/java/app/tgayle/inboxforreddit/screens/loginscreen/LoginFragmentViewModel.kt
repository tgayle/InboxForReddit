package app.tgayle.inboxforreddit.screens.loginscreen

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.model.RedditClientAccountPair
import kotlinx.coroutines.*
import net.dean.jraw.RedditClient
import net.dean.jraw.oauth.AccountHelper

class LoginFragmentViewModel(private val userRepository: UserRepository,
                             redditHelper: AccountHelper): ViewModel(), LoginScreenModel {
    private val jrawAuthHelper = redditHelper.switchToNewUser()
    private val scopes = arrayOf("modmail", "modlog", "read", "privatemessages", "report", "identity")
    val loginUrl = jrawAuthHelper.getAuthorizationUrl(true, true, *scopes)
    private val navigationDecisionMutable = MutableLiveData<LoginFragmentState>()

    override fun isValidAuthorizationLink(link: String?): Boolean {
        if (link != null && jrawAuthHelper.isFinalRedirectUrl(link)) {
            navigationDecisionMutable.value = LoginFragmentState.Loading
            GlobalScope.launch(Dispatchers.Main) {
                val redditClient = attemptLogin(link).await()
                val redditAccount = userRepository.saveUser(redditClient).await()
                userRepository.updateCurrentUser(RedditClientAccountPair(redditClient, redditAccount))
                navigationDecisionMutable.value = LoginFragmentState.NavigateHome
            }
            return true
        }
        return false
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

    fun getNavigationDecision(): LiveData<LoginFragmentState> = navigationDecisionMutable

}
package app.tgayle.inboxforreddit.screens.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.tgayle.inboxforreddit.db.repository.UserRepository
import net.dean.jraw.oauth.AccountHelper

class LoginFragmentViewModelFactory(private val userRepository: UserRepository,
                                    private val redditAccountHelper: AccountHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginFragmentViewModel::class.java)) {
            return LoginFragmentViewModel(userRepository, redditAccountHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
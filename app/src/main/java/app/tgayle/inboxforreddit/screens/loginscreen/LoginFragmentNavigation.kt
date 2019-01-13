package app.tgayle.inboxforreddit.screens.loginscreen

sealed class LoginFragmentNavigation {
    object NavigateHome : LoginFragmentNavigation()
    object Login : LoginFragmentNavigation()
    object Loading : LoginFragmentNavigation()
}
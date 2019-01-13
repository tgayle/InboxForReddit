package app.tgayle.inboxforreddit.screens.loginscreen

sealed class LoginFragmentState {
    object NavigateHome : LoginFragmentState()
    object Login : LoginFragmentState()
    object Loading : LoginFragmentState()
}
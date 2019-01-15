package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

sealed class BottomNavigationDrawerState {
    object NavigateAddAccount : BottomNavigationDrawerState()
    object Dismiss : BottomNavigationDrawerState()
}
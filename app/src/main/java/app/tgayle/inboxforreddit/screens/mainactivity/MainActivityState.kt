package app.tgayle.inboxforreddit.screens.mainactivity

sealed class MainActivityState {
    object EmptyState: MainActivityState()
    object NavigateLogin : MainActivityState()
    object RecreateActivity : MainActivityState()
    class UpdateToolbarVisibility(val visible: Boolean): MainActivityState()
}
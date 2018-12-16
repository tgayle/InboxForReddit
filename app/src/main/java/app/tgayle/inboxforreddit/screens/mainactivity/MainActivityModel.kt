package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent

interface MainActivityModel {
    interface Listener {
    }

    fun onIntentOccurred(intent: Intent)
    fun requestToolbarTitleChange(title: String?)
    fun requestChangeToolbarScrollState(enabled: Boolean)
    fun requestNavigateAddAccount()
}
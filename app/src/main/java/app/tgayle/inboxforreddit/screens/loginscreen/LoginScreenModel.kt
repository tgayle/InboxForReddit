package app.tgayle.inboxforreddit.screens.loginscreen

import android.webkit.WebView

interface LoginScreenModel {
    interface Listener {
        fun goToLoginTab(webView: WebView)
        fun observeActionDispatch()
    }

    fun onLoginOccurred(link: String)


}
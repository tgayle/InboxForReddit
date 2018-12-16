package app.tgayle.inboxforreddit.screens.loginscreen

import android.os.Bundle
import android.webkit.WebView

interface LoginScreenModel {
    interface Listener {
        fun goToLoginTab(webView: WebView)
        fun observeActionDispatch()
    }

    fun onLoginOccurred(link: String)
    fun shouldPopBackStackOnFinish(bundleArgs: Bundle?): Boolean


}
package app.tgayle.inboxforreddit.screens.loginscreen


import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import kotlinx.android.synthetic.main.fragment_login.*


class LoginScreenFragment : Fragment(), LoginScreenModel.Listener {
    lateinit var viewModelFactory: LoginFragmentViewModelFactory
    lateinit var activityViewModel: MainActivityViewModel
    lateinit var viewModel: LoginFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModelFactory = LoginFragmentViewModelFactory(AppSingleton.dataRepository)
        activityViewModel = ViewModelProviders.of(activity!!, MainActivityViewModelFactory(AppSingleton.dataRepository)).get(MainActivityViewModel::class.java)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val webView = view.findViewById<WebView>(R.id.loginWebview)
        setLoginWebviewListener(webView)
        clearWebViewHistory(webView)
        goToLoginTab(webView)
        listenForNavigation()
        return view
    }

    private fun listenForNavigation() {
        viewModel.getNavigationDecision().observe(viewLifecycleOwner, Observer {
            when (it) {
                null -> Log.d("Login", "Nav decision currently null.")
                LoginFragmentViewModel.LoginFragmentNavigation.HOME -> findNavController().navigate(LoginScreenFragmentDirections.actionLoginFragmentToHomeFragment())
                LoginFragmentViewModel.LoginFragmentNavigation.LOGIN -> {}
                LoginFragmentViewModel.LoginFragmentNavigation.LOADING -> {
                    loginWebview.visibility = View.GONE
                    loginFragmentText.text = "Loading..."
                }
            }
        })
    }

    fun setLoginWebviewListener(webView: WebView) {
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                viewModel.onLoginOccurred(url!!)
            }
        }
    }

    override fun goToLoginTab(webView: WebView) {
        webView.loadUrl(viewModel.loginUrl)
    }

    private fun clearWebViewHistory(webview: WebView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies {}
            cookieManager.removeSessionCookies { }
            cookieManager.flush()
        }
        //////////////////////////////////////////////////////////////
        webview.clearCache(true)
        webview.clearHistory()
        val webSettings = webview.settings
        webSettings.saveFormData = false
        webSettings.savePassword = false
    }
}

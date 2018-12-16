package app.tgayle.inboxforreddit.screens.loginscreen


import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.loginscreen.LoginFragmentViewModel.FragmentActions.ALERT_MAIN_VM_WITH_REDDIT
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
        goToLoginTab(webView)
        observeActionDispatch()
        listenForNavigation()
        return view
    }

    private fun listenForNavigation() {
        viewModel.getNavigationDecision().observe(this, Observer {
            when (it) {
                null -> Log.d("Login", "Nav decision currently null.")
                LoginFragmentViewModel.LoginFragmentNavigation.HOME -> {
                    if (viewModel.shouldPopBackStackOnFinish(arguments)) {
                        findNavController().popBackStack(R.id.homeFragment, true)
                    } else {
                        findNavController().navigate(R.id.homeFragment)
                    }
                }
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

    override fun observeActionDispatch() {
        viewModel.getActionDispatch().observe(this, Observer {actions ->
            when (actions) {
                ALERT_MAIN_VM_WITH_REDDIT -> activityViewModel.onRedditClientUpdated(viewModel.getRedditClient().value!!)
            }
        })
    }

    override fun goToLoginTab(webView: WebView) {
        webView.loadUrl(viewModel.loginUrl)
    }
}

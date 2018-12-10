package app.tgayle.inboxforreddit.screens.splashscreen


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import app.tgayle.inboxforreddit.screens.splashscreen.SplashFragmentViewModel.SplashScreenAction.UPDATE_ACTIVITY_VM_WITH_REDDIT

class SplashFragment : Fragment(), SplashScreenModel.Listener {
    lateinit var vmFactory: SplashFragmentViewModelFactory
    lateinit var viewModel: SplashFragmentViewModel
    lateinit var activityVm: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        vmFactory = SplashFragmentViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(SplashFragmentViewModel::class.java)
        activityVm = ViewModelProviders.of(activity!!, MainActivityViewModelFactory(AppSingleton.dataRepository)).get(MainActivityViewModel::class.java)
        super.onCreate(savedInstanceState)

        listenForNavigation()
        listenForUsers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        listenForActionDispatch()
        listenForNavigation()
        return view
    }

    override fun listenForNavigation() {
        viewModel.navigationDecision.observe(this, Observer { navigationId ->
            if (navigationId != null) {
                Log.d("Splash Emission", navigationId.toString())
                findNavController().let {
                    it.popBackStack()
                    it.navigate(navigationId)
                }
            }
        })
    }

    override fun listenForUsers() = viewModel.getAllUsers().observe(this, Observer {
        viewModel.onUsersUpdate(it)
    })

    override fun listenForActionDispatch() {
        viewModel.getViewmodelDispatch().observe(this, Observer {
            when (it) {
                UPDATE_ACTIVITY_VM_WITH_REDDIT -> {
                    viewModel.getLocatedRedditAccount().observe(this, Observer { clientAndAccount ->
                        if (clientAndAccount != null) activityVm.onRedditClientUpdated(clientAndAccount);
                    })
                }
                null -> TODO()
            }
        })
    }

}

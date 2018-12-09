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

class SplashFragment : Fragment(), SplashScreenModel.Listener {
    lateinit var vmFactory: SplashFragmentViewModelFactory
    lateinit var viewModel: SplashFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        vmFactory = SplashFragmentViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(SplashFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)

        listenForNavigation()
        listenForUsers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listenForNavigation()
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

    override fun listenForUsers() {
        viewModel.getAllUsers().observe(this, Observer {
            viewModel.onUsersUpdate(it)
        })
    }

}

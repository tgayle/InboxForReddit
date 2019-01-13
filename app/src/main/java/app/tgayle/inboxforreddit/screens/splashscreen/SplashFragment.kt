package app.tgayle.inboxforreddit.screens.splashscreen


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.screens.BaseFragment
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivity
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashFragment : BaseFragment(), SplashScreenModel.Listener {
    lateinit var vmFactory: SplashFragmentViewModelFactory
    lateinit var viewModel: SplashFragmentViewModel
    lateinit var activityVm: MainActivityViewModel
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var messageRepository: MessageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        vmFactory = SplashFragmentViewModelFactory(userRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(SplashFragmentViewModel::class.java)
        activityVm = ViewModelProviders.of(activity!!, (activity as MainActivity).vmFactory).get(MainActivityViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        activityVm.requestUpdateToolbarVisibility(false)
        listenForNavigation()
        return view
    }

    override fun listenForNavigation() {
        viewModel.navigationDecision.observe(viewLifecycleOwner, Observer { navigationId ->
            if (navigationId != null) {
                Log.d("Splash Emission", navigationId.toString())
                GlobalScope.launch(Dispatchers.Main) {
                    delay(1000)
                    findNavController().let {
                        it.popBackStack()
                        it.navigate(navigationId)
                    }
                }
            }
        })
    }

}

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
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.screens.BaseFragment
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashFragment : BaseFragment(), SplashScreenModel.Listener {
    lateinit var vmFactory: SplashFragmentViewModelFactory
    lateinit var viewModel: SplashFragmentViewModel
    lateinit var activityVm: MainActivityViewModel
    @Inject lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        vmFactory = SplashFragmentViewModelFactory(dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(SplashFragmentViewModel::class.java)
        activityVm = ViewModelProviders.of(activity!!, MainActivityViewModelFactory(dataRepository)).get(MainActivityViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

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

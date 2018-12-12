package app.tgayle.inboxforreddit.screens.homescreen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : NavHostFragment(), NavController.OnDestinationChangedListener {
    lateinit var activityViewModel: MainActivityViewModel
    lateinit var viewModelFactory: HomeFragmentViewModelFactory
    lateinit var viewModel: HomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, MainActivityViewModelFactory(AppSingleton.dataRepository)).get(
            MainActivityViewModel::class.java)
        viewModelFactory = HomeFragmentViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeFragmentViewModel::class.java)

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        setupViews(view)
        return view
    }

    fun setupViews(view: View) {
        val bottomNavigationBar = view.findViewById<BottomNavigationView>(R.id.home_bottom_nav)
        val homeScreenNavHostView = view.findViewById<View>(R.id.home_fragment_navhost)
        val homeScreenNavHost = Navigation.findNavController(homeScreenNavHostView)
        NavigationUI.setupWithNavController(bottomNavigationBar, homeScreenNavHost)
        NavigationUI.setupActionBarWithNavController(activity!! as AppCompatActivity, homeScreenNavHost)
        homeScreenNavHost.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
       viewModel.onDestinationChanged(controller, destination, arguments)
    }
}

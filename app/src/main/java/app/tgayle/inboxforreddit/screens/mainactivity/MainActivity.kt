package app.tgayle.inboxforreddit.screens.mainactivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityModel.Listener, NavController.OnDestinationChangedListener {
    lateinit var vmFactory: MainActivityViewModelFactory
    lateinit var viewModel: MainActivityViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(home_bottom_bar)
        navController = findNavController(R.id.nav_host)
        navController.addOnDestinationChangedListener(this)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration())
        vmFactory = MainActivityViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(MainActivityViewModel::class.java)
        viewModel.onIntentOccurred(intent) // Pass intent in case we start from some specific context.

        viewModel.getCurrentToolbarText().observe(this, Observer {
            main_toolbar.title = it
        })

        viewModel.getToolbarScrollEnabled().observe(this, Observer { enabled ->
            if (enabled) enableToolbarScrolling() else preventToolbarScrolling()
        })
    }

    private fun appBarConfiguration() = AppBarConfiguration.Builder(R.id.homeFragment).build()

    private fun preventToolbarScrolling() {
        val toolbarLayoutParams = main_toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags = 0
        main_toolbar.layoutParams = toolbarLayoutParams

        val appbarLayoutParams = main_appbarlayout.layoutParams as CoordinatorLayout.LayoutParams
        appbarLayoutParams.behavior = null
        main_appbarlayout.layoutParams = appbarLayoutParams
    }

    private fun enableToolbarScrolling() {
        val toolbarLayoutParams = main_toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL.or(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP)
        main_toolbar.layoutParams = toolbarLayoutParams

        val appbarLayoutParams = main_appbarlayout.layoutParams as CoordinatorLayout.LayoutParams
        appbarLayoutParams.behavior = AppBarLayout.Behavior()
        main_appbarlayout.layoutParams = appbarLayoutParams
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        destination.id.let {
            if (it == R.id.loginFragment || it == R.id.loginWebview) {
                hideWindowBarsAndFab()
            } else {
                showWindowBarsAndFab()
            }
        }
    }

    private fun hideWindowBarsAndFab() {
        main_toolbar.visibility = View.GONE
        home_bottom_bar.visibility = View.GONE
        home_fab.hide()
    }

    private fun showWindowBarsAndFab() {
        main_toolbar.visibility = View.VISIBLE
        home_bottom_bar.visibility = View.VISIBLE
        home_fab.show()
    }
}

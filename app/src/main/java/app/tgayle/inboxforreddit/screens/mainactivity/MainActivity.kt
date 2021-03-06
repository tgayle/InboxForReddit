package app.tgayle.inboxforreddit.screens.mainactivity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.screens.loginscreen.LoginScreenFragmentArgs
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragment
import com.google.android.material.appbar.AppBarLayout
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityModel.Listener,
    NavController.OnDestinationChangedListener, HasSupportFragmentInjector {

    lateinit var vmFactory: MainActivityViewModelFactory
    lateinit var viewModel: MainActivityViewModel
    lateinit var navController: NavController
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        vmFactory = MainActivityViewModelFactory(messageRepository, userRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(MainActivityViewModel::class.java)
        this.setTheme(if (viewModel.nightModeEnabled) R.style.DankTheme else R.style.InboxTheme)
        // TODO: Persist night node
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(home_bottom_bar)

        navController = findNavController(R.id.nav_host)
        navController.addOnDestinationChangedListener(this)

        viewModel.onIntentOccurred(intent) // Pass intent in case we start from some specific context.

        viewModel.getCurrentToolbarText().observe(this, Observer {
            main_toolbar.title = it
        })

        viewModel.getToolbarScrollEnabled().observe(this, Observer { enabled ->
            if (enabled) enableToolbarScrolling() else preventToolbarScrolling()
        })

        main_toolbar.setupWithNavController(navController, appBarConfiguration())
        main_toolbar.setOnClickListener {
            viewModel.themeChangeRequested()
        }

        viewModel.getActionDispatch().observe(this, Observer {
            when (it) {
                is MainActivityState.NavigateLogin -> {
                    val args = LoginScreenFragmentArgs.Builder().setPopBackStackAfterLogin(true).build().toBundle()
                    navController.navigate(R.id.action_global_loginFragment, args)
                }
                is MainActivityState.RecreateActivity -> recreate()
                is MainActivityState.EmptyState -> return@Observer
                is MainActivityState.UpdateToolbarVisibility -> if (it.visible) showWindowBarsAndFab() else hideWindowBarsAndFab()
            }
            viewModel.onActionAcknowledged()
        })

    }

    private fun appBarConfiguration() = AppBarConfiguration.Builder(R.id.homeFragment, R.id.inboxFragment).build()

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
        toolbarLayoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }
        return false
    }

    private fun hideWindowBarsAndFab() {
        main_toolbar.visibility = View.GONE
        home_bottom_bar.visibility = View.GONE
    }

    private fun showWindowBarsAndFab() {
        main_toolbar.visibility = View.VISIBLE
        home_bottom_bar.visibility = View.VISIBLE
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector
}

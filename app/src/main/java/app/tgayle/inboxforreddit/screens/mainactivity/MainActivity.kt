package app.tgayle.inboxforreddit.screens.mainactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R

class MainActivity : AppCompatActivity(), MainActivityModel.Listener {
    lateinit var vmFactory: MainActivityViewModelFactory
    lateinit var viewModel: MainActivityViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration())
        vmFactory = MainActivityViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(MainActivityViewModel::class.java)
        viewModel.onIntentOccurred(intent) // Pass intent in case we start from some specific context.

    }

    private fun appBarConfiguration() = AppBarConfiguration.Builder(R.id.homeFragment).build()
}

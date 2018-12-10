package app.tgayle.inboxforreddit.screens.homescreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory

/**
 * A base Fragment to be implemented by any child fragments that appear in [HomeFragment]
 */
abstract class BaseHomeScreenFragment(): Fragment() {
    protected lateinit var activityViewModel: MainActivityViewModel
    protected lateinit var parentViewModel: HomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProviders
            .of(this, HomeFragmentViewModelFactory(AppSingleton.dataRepository))
            .get(HomeFragmentViewModel::class.java)

        activityViewModel = ViewModelProviders
            .of(activity!!, MainActivityViewModelFactory(AppSingleton.dataRepository))
            .get(MainActivityViewModel::class.java)
    }
}
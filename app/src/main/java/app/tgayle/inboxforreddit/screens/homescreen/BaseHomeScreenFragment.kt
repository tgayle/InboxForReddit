package app.tgayle.inboxforreddit.screens.homescreen

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.screens.BaseFragment
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import javax.inject.Inject

/**
 * A base Fragment to be implemented by any child fragments that appear in [HomeFragment]
 */
abstract class BaseHomeScreenFragment: BaseFragment() {
    protected lateinit var activityViewModel: MainActivityViewModel
    protected lateinit var parentViewModel: HomeFragmentViewModel
    @Inject lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProviders
            .of(this, HomeFragmentViewModelFactory(dataRepository))
            .get(HomeFragmentViewModel::class.java)

        activityViewModel = ViewModelProviders
            .of(activity!!, MainActivityViewModelFactory(dataRepository))
            .get(MainActivityViewModel::class.java)
    }
}
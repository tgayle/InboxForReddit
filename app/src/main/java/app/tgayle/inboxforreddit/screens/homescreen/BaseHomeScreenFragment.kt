package app.tgayle.inboxforreddit.screens.homescreen

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.screens.BaseFragment
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivity
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import javax.inject.Inject

/**
 * A base Fragment to be implemented by any child fragments that appear in [HomeFragment]
 */
abstract class BaseHomeScreenFragment: BaseFragment() {
    protected lateinit var activityViewModel: MainActivityViewModel
    protected lateinit var parentViewModel: HomeFragmentViewModel
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProviders
            .of(this, HomeFragmentViewModelFactory(messageRepository))
            .get(HomeFragmentViewModel::class.java)

        activityViewModel = ViewModelProviders
            .of(activity!!, (activity as MainActivity).vmFactory)
            .get(MainActivityViewModel::class.java)
    }
}
package app.tgayle.inboxforreddit.screens.homescreen


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivity
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class HomeFragment : NavHostFragment() {
    lateinit var activityViewModel: MainActivityViewModel
    lateinit var viewModelFactory: HomeFragmentViewModelFactory
    lateinit var viewModel: HomeFragmentViewModel
    @Inject lateinit var messageRepository: MessageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, (activity as MainActivity).vmFactory).get(
            MainActivityViewModel::class.java)
        viewModelFactory = HomeFragmentViewModelFactory(messageRepository)
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
        val homeScreenNavHostView = view.findViewById<View>(R.id.home_fragment_navhost)
        val homeScreenNavHost = Navigation.findNavController(homeScreenNavHostView)
        //TODO:  Toolbar Button Functionality
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModelFactory
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragmentViewModel.BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ACCOUNT_SWITCH
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragmentViewModel.BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.main_bottom_nav_drawer.*

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {
    private val vmFactory = BottomNavigationDrawerFragmentViewModelFactory(AppSingleton.dataRepository)
    lateinit var activityVm: MainActivityViewModel
    lateinit var viewModel: BottomNavigationDrawerFragmentViewModel
    val usersAdapter by lazy { UserListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVm = ViewModelProviders.of(activity!!, MainActivityViewModelFactory(AppSingleton.dataRepository)).get(MainActivityViewModel::class.java)
        viewModel = ViewModelProviders.of(this, vmFactory).get(BottomNavigationDrawerFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_bottom_nav_drawer, container, false)

        activityVm.getRedditClient().observe(this, Observer {
            bottom_nav_username.text = it.second.name
        })

        viewModel.getUsersList().observe(this, Observer {
            usersAdapter.submitItems(it)
        })

        viewModel.getActionDispatch().observe(this, Observer {
            if (it == null) return@Observer

            when (it.first) {
                NOTIFY_MAIN_ACTIVITY_FOR_ACCOUNT_SWITCH -> {
                    activityVm.requestAccountSwitch(it.second)
                }
                NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT -> {
                    activityVm.requestNavigateAddAccount()
                }
            }
            dismiss()
            viewModel.onActionDispatchAcknowledged()
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottom_nav_users_list.adapter = usersAdapter
        bottom_nav_users_list.layoutManager = LinearLayoutManager(context)
        usersAdapter.setOnClickListener { position, totalSize, user ->
            viewModel.onUserListClick(position, totalSize, user)
        }

        main_bottom_navigation_view.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener false
        }
    }
}
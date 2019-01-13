package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivity
import app.tgayle.inboxforreddit.screens.mainactivity.MainActivityViewModel
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragmentViewModel.BottomNavigationDrawerAction.DISMISS
import app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer.BottomNavigationDrawerFragmentViewModel.BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT
import app.tgayle.inboxforreddit.util.getColorFromAttr
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.main_bottom_nav_drawer.*
import javax.inject.Inject


class BottomNavigationDrawerFragment: BottomSheetDialogFragment(), NavigationView.OnNavigationItemSelectedListener {
    @Inject lateinit var userRepository: UserRepository
    private lateinit var activityVm: MainActivityViewModel
    private lateinit var viewModel: BottomNavigationDrawerFragmentViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var vmFactory: BottomNavigationDrawerFragmentViewModelFactory

    private val usersAdapter by lazy { UserListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmFactory = BottomNavigationDrawerFragmentViewModelFactory(userRepository)
        activityVm = ViewModelProviders.of(activity!!, (activity!! as MainActivity).vmFactory).get(MainActivityViewModel::class.java)
        viewModel = ViewModelProviders.of(this, vmFactory).get(BottomNavigationDrawerFragmentViewModel::class.java)
        layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_bottom_nav_drawer, container, false)
        viewModel.getCurrentUser().observe(viewLifecycleOwner, Observer {
            bottom_nav_username.text = it.account?.name
        })

        viewModel.getUsersList().observe(viewLifecycleOwner, Observer {
            usersAdapter.submitList(it)
        })

        viewModel.getActionDispatch().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            when (it) {
                NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT -> {
                    activityVm.requestNavigateAddAccount()
                }
                DISMISS -> {}
            }
            dismiss()
            viewModel.onActionDispatchAcknowledged()
        })

        /* XML background attribute doesn't seem to work on bottom sheets so we set it here.*/
        view.setBackgroundColor(context!!.getColorFromAttr(android.R.attr.windowBackground))

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottom_nav_users_list.adapter = usersAdapter
        bottom_nav_users_list.layoutManager = layoutManager

        add_user_list_item_root.setOnClickListener {
            viewModel.onUserAddClick()
        }

        main_bottom_navigation_view.setNavigationItemSelectedListener(this)

        usersAdapter.onUserClick = {user -> viewModel.onUserListClick(user) }

        usersAdapter.onUserRemoveClick = {user -> viewModel.onUserRemoveClick(user) }

        usersAdapter.onUserAddClick = { viewModel.onUserAddClick() }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toggle_night_mode -> {
                activityVm.themeChangeRequested()
                true
            }
            else -> false
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}
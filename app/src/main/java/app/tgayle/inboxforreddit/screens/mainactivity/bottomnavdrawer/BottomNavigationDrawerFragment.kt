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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.main_bottom_nav_drawer.*

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {
    private val vmFactory = BottomNavigationDrawerFragmentViewModelFactory(AppSingleton.dataRepository)
    lateinit var viewModel: BottomNavigationDrawerFragmentViewModel
    val usersAdapter by lazy { UserListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory).get(BottomNavigationDrawerFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_bottom_nav_drawer, container, false)

        viewModel.getUsersList().observe(this, Observer {
            usersAdapter.submitItems(it)
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottom_nav_users_list.adapter = usersAdapter
        bottom_nav_users_list.layoutManager = LinearLayoutManager(context)
        main_bottom_navigation_view.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener false
        }
    }
}
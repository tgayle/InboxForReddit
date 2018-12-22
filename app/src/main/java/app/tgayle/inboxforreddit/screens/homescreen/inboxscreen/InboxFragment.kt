package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.InboxFragmentViewModel.InboxFragmentAction.NAVIGATE_TO_CONVERSATION
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view.PagedMessageAdapter
import kotlinx.android.synthetic.main.inbox_fragment.*
import kotlinx.android.synthetic.main.inbox_fragment.view.*

class InboxFragment : BaseHomeScreenFragment(), InboxScreenModel.Listener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: InboxFragmentViewModel
    private lateinit var rvAdapter: PagedMessageAdapter
    private lateinit var rvLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders
            .of(this, InboxFragmentViewModelFactory(AppSingleton.dataRepository))
            .get(InboxFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.inbox_fragment, container, false)
        setHasOptionsMenu(true)
        rvAdapter = PagedMessageAdapter()
        rvLayoutManager = LinearLayoutManager(context)

        view.inbox_fragment_refresh.setOnRefreshListener { onRefresh() }
        view.inbox_fragment_messageRv.adapter = rvAdapter
        view.inbox_fragment_messageRv.layoutManager = rvLayoutManager

        rvAdapter.onMessageClickListener = {
            viewModel.onMessageClicked(it)
        }

        viewModel.currentUser.observe(this, Observer {
            viewModel.onRefresh(viewModel.currentUser.value, false)
        })

        viewModel.getInboxFromClientAndAccount(viewModel.currentUser).observe(this, Observer {
            rvAdapter.submitList(it)
            /*
            Use doOnNextLayout to make sure items have finished being laid out in the view so we can accurately decide if
            the toolbar should be locked and if the last item is visible.
             */
            inbox_fragment_messageRv.doOnNextLayout {
                val isLastItemVisible = rvLayoutManager.findLastCompletelyVisibleItemPosition() == rvAdapter.itemCount - 1
                val toolbarScrollingEnabled = viewModel.shouldRequestPreventToolbarScroll(isLastItemVisible)
                activityViewModel.requestChangeToolbarScrollState(toolbarScrollingEnabled)
            }
        })

        viewModel.getRefreshing().observe(this, Observer {
            inbox_fragment_refresh.isRefreshing = it
        })

        viewModel.getCurrentFilterTitle().observe(this, Observer {
            activityViewModel.requestToolbarTitleChange(it)
        })

        viewModel.getActionDispatch().observe(this, Observer {
            if (it == null) return@Observer

            when (it.first) {
                NAVIGATE_TO_CONVERSATION -> {
                    val navigationArgs = InboxFragmentDirections.ActionInboxFragmentToConversationFragment(it.second!!.parentName)
                    findNavController().navigate(navigationArgs)
                }
            }

            viewModel.onActionAcknowledged()
        })

        return view
    }

    override fun onRefresh() {
        viewModel.onRefresh(viewModel.currentUser.value, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_bottom_navbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.appbar_filter -> {
                val popupMenu = PopupMenu(context, activity!!.findViewById(R.id.appbar_filter))
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.filter_options, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(this)
                popupMenu.show()
            }
            R.id.appbar_search -> { // Enable Search?
            }
        }
        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return viewModel.onFilterSelection(item?.itemId)
    }
}

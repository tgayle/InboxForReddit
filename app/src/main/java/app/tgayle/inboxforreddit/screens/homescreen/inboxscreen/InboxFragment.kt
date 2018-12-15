package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view.MessageRecyclerViewAdapter
import kotlinx.android.synthetic.main.inbox_fragment.*

class InboxFragment : BaseHomeScreenFragment(), InboxScreenModel.Listener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: InboxFragmentViewModel
    private lateinit var rvAdapter: MessageRecyclerViewAdapter
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
        rvAdapter = MessageRecyclerViewAdapter()
        rvLayoutManager = LinearLayoutManager(context)

        viewModel.getInboxFromClientAndAccount(activityViewModel.getRedditClient()).observe(this, Observer {
            rvAdapter.resetItems(it)
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inbox_fragment_refresh.setOnRefreshListener { onRefresh() }
        inbox_fragment_messageRv.adapter = rvAdapter
        inbox_fragment_messageRv.layoutManager = rvLayoutManager
    }

    override fun onResume() {
        viewModel.onRefresh(activityViewModel.getRedditClient().value, false)
        super.onResume()
    }

    override fun onRefresh() {
        viewModel.onRefresh(activityViewModel.getRedditClient().value, true)
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

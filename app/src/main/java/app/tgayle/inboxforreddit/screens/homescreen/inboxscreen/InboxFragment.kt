package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view.PagedMessageAdapter
import kotlinx.android.synthetic.main.inbox_fragment.*
import kotlinx.android.synthetic.main.inbox_fragment.view.*

class InboxFragment : BaseHomeScreenFragment(), InboxScreenModel.Listener, PopupMenu.OnMenuItemClickListener {
    private lateinit var viewModel: InboxFragmentViewModel
    private lateinit var rvAdapter: PagedMessageAdapter
    private lateinit var rvLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders
            .of(this, InboxFragmentViewModelFactory(messageRepository, userRepository))
            .get(InboxFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.inbox_fragment, container, false)
        rvAdapter = PagedMessageAdapter()
        rvLayoutManager = LinearLayoutManager(context)

        view.inbox_fragment_refresh.setOnRefreshListener { onRefresh() }
        view.inbox_fragment_messageRv.adapter = rvAdapter
        view.inbox_fragment_messageRv.layoutManager = rvLayoutManager

        rvAdapter.onMessageClickListener = {
            viewModel.onMessageClicked(it)
        }

        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            viewModel.onRefresh(viewModel.currentUser.value, false)
        })

        viewModel.getInboxFromClientAndAccount(viewModel.currentUser).observe(viewLifecycleOwner, Observer {
            rvAdapter.submitList(it)
            /*
            Use doOnNextLayout to make sure items have finished being laid out in the view so we can accurately decide if
            the toolbar should be locked and if the last item is visible.
             */
            inbox_fragment_messageRv.doOnNextLayout {view ->
                val isLastItemVisible = rvLayoutManager.findLastCompletelyVisibleItemPosition() == rvAdapter.itemCount - 1
                val toolbarScrollingEnabled = viewModel.shouldRequestPreventToolbarScroll(isLastItemVisible)
                activityViewModel.requestChangeToolbarScrollState(toolbarScrollingEnabled)
                /* Scroll back to last position if we return to this screen */
                viewModel.inboxViewState.run {
                    // Don't try to scroll back to last position if we've switched accounts.
                    if (lastAccount?.account?.name == viewModel.currentUser.value?.account?.name) {
                        view.post {
                            rvLayoutManager.scrollToPositionWithOffset(firstVisibleMessagePosition, viewOffset)
                        }
                    }
                }
            }
        })

        viewModel.getRefreshing().observe(viewLifecycleOwner, Observer {
            inbox_fragment_refresh.isRefreshing = it
        })

        viewModel.getCurrentFilterTitle().observe(viewLifecycleOwner, Observer {
            activityViewModel.requestToolbarTitleChange(it)
        })

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            when (it) {
                is InboxFragmentState.NavigateConversation -> {
                    val navigationArgs = InboxFragmentDirections.ActionInboxFragmentToConversationFragment(it.message.parentName)
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

    override fun onStop() {
        val firstVisibleMessagePosition = rvLayoutManager.findFirstVisibleItemPosition()
        val itemTop = rvLayoutManager.findViewByPosition(firstVisibleMessagePosition)?.top ?: 0
        viewModel.onFragmentStop(InboxScreenStateArgs(firstVisibleMessagePosition, itemTop))
        super.onStop()
    }
}

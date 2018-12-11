package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.inboxscreen.view.MessageRecyclerViewAdapter
import kotlinx.android.synthetic.main.inbox_fragment.*

class InboxFragment : BaseHomeScreenFragment(), InboxScreenModel.Listener {
    private lateinit var viewModel: InboxFragmentViewModel
    private lateinit var rvAdapter: MessageRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders
            .of(this, InboxFragmentViewModelFactory(AppSingleton.dataRepository))
            .get(InboxFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.inbox_fragment, container, false)
        rvAdapter = MessageRecyclerViewAdapter()

        viewModel.getInboxFromClientAndAccount(activityViewModel.getRedditClient()).observe(this, Observer {
            rvAdapter.resetItems(it)
        })

        viewModel.getRefreshing().observe(this, Observer {
            inbox_fragment_refresh.isRefreshing = it
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inbox_fragment_refresh.setOnRefreshListener { onRefresh() }
        inbox_fragment_messageRv.adapter = rvAdapter
        inbox_fragment_messageRv.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        onRefresh()
        super.onResume()
    }

    override fun onRefresh() {
        viewModel.onRefresh(activityViewModel.getRedditClient().value)
    }

}

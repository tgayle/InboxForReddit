package app.tgayle.inboxforreddit.screens.homescreen.inboxscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import kotlinx.android.synthetic.main.inbox_fragment.*

class InboxFragment : BaseHomeScreenFragment() {
    private lateinit var viewModel: InboxFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders
            .of(this, InboxFragmentViewModelFactory(AppSingleton.dataRepository))
            .get(InboxFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.inbox_fragment, container, false)
        viewModel.getInboxFromClientAndAccount(activityViewModel.getRedditClient()).observe(this, Observer {
            inbox_text.text = it.size.toString() + it.map { "${it.author}, ${it.destination}, ${it.fullName}" }.toString()
        })
        return view
    }

}

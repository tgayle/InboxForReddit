package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view.ConversationMessagePagedAdapter
import kotlinx.android.synthetic.main.conversation_fragment.view.*

class ConversationFragment : BaseHomeScreenFragment() {
    private lateinit var viewModel: ConversationViewModel
    private lateinit var vmFactory: ConversationViewModelFactory
    private val adapter = ConversationMessagePagedAdapter()
    private val layoutManager = LinearLayoutManager(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conversationParentName = arguments?.getString("parentName")
            ?: throw IllegalArgumentException("Required conversation parent name was null")

        vmFactory = ConversationViewModelFactory(conversationParentName, AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(ConversationViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        viewModel.conversationInfo.observe(this, Observer {
            if (it == null) return@Observer
            activityViewModel.requestToolbarTitleChange(it.subject)
        })

        viewModel.getConversationMessages().observe(this, Observer {
            adapter.submitList(it)
            view.conversation_detail_rv.doOnNextLayout {
                layoutManager.scrollToPositionWithOffset(adapter.itemCount - 1, 1000)
                startPostponedEnterTransition()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.conversation_fragment, container, false)
        view.conversation_detail_rv.layoutManager = layoutManager
        view.conversation_detail_rv.adapter = adapter
        return view
    }

}

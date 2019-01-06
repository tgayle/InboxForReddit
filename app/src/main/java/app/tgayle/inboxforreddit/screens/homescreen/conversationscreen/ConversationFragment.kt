package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view.ConversationMessagePagedAdapter
import app.tgayle.inboxforreddit.util.MaterialSnackbar
import kotlinx.android.synthetic.main.conversation_fragment.view.*

class ConversationFragment : BaseHomeScreenFragment() {
    private lateinit var viewModel: ConversationViewModel
    private lateinit var vmFactory: ConversationViewModelFactory
    private val messagesAdapter = ConversationMessagePagedAdapter()
    private val messagesLayoutManager = LinearLayoutManager(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
            messagesAdapter.submitList(it)
            view.conversation_detail_rv.doOnNextLayout {
                messagesLayoutManager.scrollToPositionWithOffset(messagesAdapter.itemCount - 1, 1000)
                startPostponedEnterTransition()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.conversation_fragment, container, false)
        view.conversation_detail_rv.layoutManager = messagesLayoutManager
        view.conversation_detail_rv.adapter = messagesAdapter
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.conversation_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.scroll_top_bottom -> {
                MaterialSnackbar.make(view!!, "Scroll not yet implemented yet! c:", MaterialSnackbar.SnackbarLength.SHORT).show()
                true
            }
            R.id.search -> {
                MaterialSnackbar.make(view!!, "Search not implemented yet! c:", MaterialSnackbar.SnackbarLength.SHORT).show()
                true
            }
            R.id.reply -> {
                MaterialSnackbar.make(view!!, "Not implemented yet! c:", MaterialSnackbar.SnackbarLength.SHORT).show()
                true
            }
            else -> false
        }
    }

}

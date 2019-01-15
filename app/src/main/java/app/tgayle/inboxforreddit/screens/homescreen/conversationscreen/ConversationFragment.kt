package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.homescreen.BaseHomeScreenFragment
import app.tgayle.inboxforreddit.screens.homescreen.conversationscreen.view.ConversationMessageAdapter
import app.tgayle.inboxforreddit.util.MaterialSnackbar
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import kotlinx.android.synthetic.main.conversation_fragment.view.*

class ConversationFragment : BaseHomeScreenFragment() {
    private lateinit var viewModel: ConversationViewModel
    private lateinit var vmFactory: ConversationViewModelFactory
    private val messagesAdapter = ConversationMessageAdapter()
    private val messagesLayoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val conversationParentName = arguments?.getString("parentName")
            ?: throw IllegalArgumentException("Required conversation parent name was null")

        vmFactory = ConversationViewModelFactory(conversationParentName, messageRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(ConversationViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.conversationInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            activityViewModel.requestToolbarTitleChange(it.subject)
        })

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                is ConversationFragmentState.ToggleListItemCollapse -> {
//                    val itemDistanceFromTopOfScreen = findDistanceFromTopOfWindow(it.position)
//                    if (itemDistanceFromTopOfScreen != null) {
//                        conversation_detail_rv.smoothScrollBy(it.position, itemDistanceFromTopOfScreen)
//                    }
                    messagesAdapter.updateItem(it.newItemState, it.position)
                }
            }
        })

        viewModel.getConversationMessages().observe(viewLifecycleOwner, Observer {
            messagesAdapter.submitItems(it)
            (view.parent as? ViewGroup)?.doOnPreDraw {
                // Parent has been drawn. Start transitioning!
//                startPostponedEnterTransition()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.conversation_fragment, container, false)
        view.conversation_detail_rv.layoutManager = messagesLayoutManager
        view.conversation_detail_rv.adapter = messagesAdapter
        messagesAdapter.onHideRevealButtonPressed = {itemWithState, adapterPosition ->  viewModel.onHideRevealButtonPressed(itemWithState, adapterPosition)}
        view.conversation_detail_rv.itemAnimator = SlideDownAlphaAnimator().apply {
            supportsChangeAnimations = false
            addDuration = 250
            removeDuration = 250
            withInterpolator(FastOutSlowInInterpolator())
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.hasFragmentFirstOpenOccurred) {
            messagesLayoutManager.scrollToPositionWithOffset(messagesAdapter.itemCount - 1, 100)
            viewModel.onFragmentFirstOpenOccurred()
        }
    }

    /**
     * Checks if the top of a item is visible on screen and returns the distance from the top of the screen.
     */
    private fun findDistanceFromTopOfWindow(position: Int): Int? {
        val firstCompletelyVisiblePos = messagesLayoutManager.findFirstCompletelyVisibleItemPosition()
        val commentExtendsBeyondWindowTopEdge = firstCompletelyVisiblePos == -1 || firstCompletelyVisiblePos > position
        var viewTop: Int? = null
        if (commentExtendsBeyondWindowTopEdge) {
            val view = messagesLayoutManager.findViewByPosition(position)
            viewTop = view?.top
        }
        return viewTop
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

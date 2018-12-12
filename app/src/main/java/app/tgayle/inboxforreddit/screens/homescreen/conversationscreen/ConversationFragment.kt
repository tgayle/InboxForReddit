package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.R

class ConversationFragment : Fragment() {
private lateinit var viewModel: ConversationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.conversation_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ConversationViewModel::class.java)

        return view
    }

}

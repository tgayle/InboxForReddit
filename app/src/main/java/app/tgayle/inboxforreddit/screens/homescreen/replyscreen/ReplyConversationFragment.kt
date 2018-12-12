package app.tgayle.inboxforreddit.screens.homescreen.replyscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.R

class ReplyConversationFragment : Fragment() {
 private lateinit var viewModel: ReplyConversationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reply_conversation_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ReplyConversationViewModel::class.java)
        return view
    }
}

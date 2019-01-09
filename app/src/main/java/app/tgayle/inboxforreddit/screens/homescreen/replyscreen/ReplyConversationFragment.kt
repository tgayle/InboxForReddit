package app.tgayle.inboxforreddit.screens.homescreen.replyscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.screens.BaseFragment

class ReplyConversationFragment : BaseFragment() {
 private lateinit var viewModel: ReplyConversationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reply_conversation_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ReplyConversationViewModel::class.java)
        return view
    }
}

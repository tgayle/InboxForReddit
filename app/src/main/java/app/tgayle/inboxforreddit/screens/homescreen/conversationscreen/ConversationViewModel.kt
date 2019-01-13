package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.model.ConversationSubjectAndCorrespondent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConversationViewModel(private val conversationParentId: String, private val messageRepository: MessageRepository) : ViewModel() {
    val conversationInfo = MutableLiveData<ConversationSubjectAndCorrespondent?>()
    var hasFragmentFirstOpenOccurred = false
    private set

    init {
        GlobalScope.launch {
            val firstMessageOfConversation = messageRepository.getFirstMessageOfConversation(conversationParentId)
            firstMessageOfConversation.let {
                conversationInfo.postValue(
                    ConversationSubjectAndCorrespondent(
                        it?.subject ?: "null", it?.correspondent ?: "null"
                    )
                )
            }
        }
    }

    fun getConversationMessages() = messageRepository.getConversationMessages(conversationParentId)
    fun onFragmentFirstOpenOccurred() {
        hasFragmentFirstOpenOccurred = true
    }
}

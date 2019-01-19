package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.model.ConversationSubjectAndCorrespondent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConversationViewModel(private val conversationParentId: String, private val messageRepository: MessageRepository) : ViewModel() {
    val conversationInfo = MutableLiveData<ConversationSubjectAndCorrespondent?>()
    private val state: MutableLiveData<ConversationFragmentState> = MutableLiveData()
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

    fun getState(): LiveData<ConversationFragmentState> = state
    fun getConversationMessages() = messageRepository.getConversationMessagesWithoutPaging(conversationParentId)

    fun onFragmentFirstOpenOccurred() {
        hasFragmentFirstOpenOccurred = true
    }

}

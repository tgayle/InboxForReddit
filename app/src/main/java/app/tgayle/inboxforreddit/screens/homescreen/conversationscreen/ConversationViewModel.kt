package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class ConversationSubjectAndCorrespondent(val subject: String, val correspondent: String)

class ConversationViewModel(private val conversationParentId: String, private val dataRepository: DataRepository) : ViewModel() {
    val conversationInfo = MutableLiveData<ConversationSubjectAndCorrespondent?>()

    init {
        GlobalScope.launch {
            val firstMessageOfConversation = dataRepository.getFirstMessageOfConversation(conversationParentId)
            firstMessageOfConversation.let {
                conversationInfo.postValue(ConversationSubjectAndCorrespondent(it?.subject ?: "null", it?.correspondent ?: "null"))
            }
        }
    }

    fun getConversationMessages() = dataRepository.getConversationMessages(conversationParentId)
}

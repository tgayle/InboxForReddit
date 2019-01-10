package app.tgayle.inboxforreddit.screens.homescreen.conversationscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.ConversationSubjectAndCorrespondent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConversationViewModel(private val conversationParentId: String, private val dataRepository: DataRepository) : ViewModel() {
    val conversationInfo = MutableLiveData<ConversationSubjectAndCorrespondent?>()
    var hasFragmentFirstOpenOccurred = false

    init {
        GlobalScope.launch {
            val firstMessageOfConversation = dataRepository.getFirstMessageOfConversation(conversationParentId)
            firstMessageOfConversation.let {
                conversationInfo.postValue(
                    ConversationSubjectAndCorrespondent(
                        it?.subject ?: "null", it?.correspondent ?: "null"
                    )
                )
            }
        }
    }

    fun getConversationMessages() = dataRepository.getConversationMessages(conversationParentId)
    fun onFragmentFirstOpenOccurred() {
        hasFragmentFirstOpenOccurred = true
    }
}

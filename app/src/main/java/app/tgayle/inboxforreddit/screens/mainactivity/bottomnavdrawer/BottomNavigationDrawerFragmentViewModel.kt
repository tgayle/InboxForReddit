package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BottomNavigationDrawerFragmentViewModel(val dataRepository: DataRepository): ViewModel() {
    private val actionDispatch = MutableLiveData<BottomNavigationDrawerAction?>()
    fun getCurrentUser() = dataRepository.getCurrentRedditUser()

    fun getActionDispatch(): LiveData<BottomNavigationDrawerAction?> = actionDispatch
    fun getUsersList() = dataRepository.getUsersAndUnreadMessageCountForEach()
    fun onUserListClick(position: Int, totalSize: Int, user: RedditUsernameAndUnreadMessageCount?) {
         if (position == totalSize - 1 && user == null) {
             actionDispatch.value = BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT
             return
        } else {
             GlobalScope.launch {
                 val userInfo = dataRepository.getClientFromUser(user!!.username).await()
                 dataRepository.updateCurrentUser(userInfo)
                 actionDispatch.postValue(BottomNavigationDrawerAction.DISMISS)
             }
        }
    }

    fun onActionDispatchAcknowledged() {
        actionDispatch.value = null
    }

    enum class BottomNavigationDrawerAction {
        NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT,
        DISMISS
    }
}
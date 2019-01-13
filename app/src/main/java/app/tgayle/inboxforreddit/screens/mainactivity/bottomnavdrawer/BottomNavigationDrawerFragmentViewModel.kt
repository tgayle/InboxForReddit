package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BottomNavigationDrawerFragmentViewModel(private val userRepository: UserRepository): ViewModel() {
    private val actionDispatch = MutableLiveData<BottomNavigationDrawerAction?>()
    fun getCurrentUser() = userRepository.getCurrentRedditUser()

    fun getActionDispatch(): LiveData<BottomNavigationDrawerAction?> = actionDispatch
    fun getUsersList() = userRepository.getUsersAndUnreadMessageCountForEach()
    fun onUserListClick(user: RedditUsernameAndUnreadMessageCount?) {
             GlobalScope.launch {
                 val userInfo = userRepository.getClientFromUser(user!!.username).await()
                 userRepository.updateCurrentUser(userInfo, true)
                 actionDispatch.postValue(BottomNavigationDrawerAction.DISMISS)
             }
    }

    fun onActionDispatchAcknowledged() {
        actionDispatch.value = null
    }

    fun onUserAddClick() {
        actionDispatch.value = BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT
    }

    fun onUserRemoveClick(user: RedditUsernameAndUnreadMessageCount?) {
        GlobalScope.launch {
            val currentUsername = getCurrentUser().value?.account?.name
            if (currentUsername == user?.username) {
                val allUsers = userRepository.getUsersDeferred().await()
                val accountToSwitchTo = allUsers.firstOrNull { it.name != currentUsername}
                if (accountToSwitchTo == null) {
                    TODO("Go back to login screen")
                } else {
                    userRepository.updateCurrentUser(userRepository.getClientFromUser(accountToSwitchTo).await(), true)
                }
            }
            userRepository.removeUser(user?.username).await()
        }
    }

    enum class BottomNavigationDrawerAction {
        NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT,
        DISMISS
    }
}
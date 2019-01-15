package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BottomNavigationDrawerFragmentViewModel(private val userRepository: UserRepository): ViewModel() {
    private val actionDispatch = MutableLiveData<BottomNavigationDrawerState?>()
    fun getCurrentUser() = userRepository.getCurrentRedditUser()

    fun getActionDispatch(): LiveData<BottomNavigationDrawerState?> = actionDispatch
    fun getUsersList() = userRepository.getUsersAndUnreadMessageCountForEach()
    fun onUserListClick(user: RedditUsernameAndUnreadMessageCount?) {
             GlobalScope.launch {
                 val userInfo = userRepository.getClientFromUser(user!!.username).await()
                 userRepository.updateCurrentUser(userInfo, true)
                 actionDispatch.postValue(BottomNavigationDrawerState.Dismiss)
             }
    }

    fun onActionDispatchAcknowledged() {
        actionDispatch.value = null
    }

    fun onUserAddClick() {
        actionDispatch.value = BottomNavigationDrawerState.NavigateAddAccount
    }

    fun onUserRemoveClick(user: RedditUsernameAndUnreadMessageCount?) {
        GlobalScope.launch {
            val currentUsername = getCurrentUser().value?.account?.name
            if (currentUsername == user?.username) {
                val allUsers = userRepository.getUsersDeferred().await()
                val accountToSwitchTo = allUsers.firstOrNull { it.name != currentUsername}
                if (accountToSwitchTo == null) {
                    actionDispatch.postValue(BottomNavigationDrawerState.NavigateAddAccount)
                } else {
                    userRepository.updateCurrentUser(userRepository.getClientFromUser(accountToSwitchTo).await(), true)
                }
            }
            userRepository.removeUser(user?.username).await()
        }
    }

}
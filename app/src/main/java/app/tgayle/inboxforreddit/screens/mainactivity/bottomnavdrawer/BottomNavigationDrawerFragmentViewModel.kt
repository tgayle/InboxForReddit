package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount

class BottomNavigationDrawerFragmentViewModel(val dataRepository: DataRepository): ViewModel() {
    private val actionDispatch = MutableLiveData<Pair<BottomNavigationDrawerAction?, String?>?>()

    fun getActionDispatch(): LiveData<Pair<BottomNavigationDrawerAction?, String?>?> = actionDispatch
    fun getUsersList() = dataRepository.getUsersAndUnreadMessageCountForEach()
    fun onUserListClick(position: Int, totalSize: Int, user: RedditUsernameAndUnreadMessageCount?) {
        val shouldAddAccount = position == totalSize - 1 && user == null
        actionDispatch.value = if (shouldAddAccount) {
            Pair(BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT, null)
        } else {
            Pair(BottomNavigationDrawerAction.NOTIFY_MAIN_ACTIVITY_FOR_ACCOUNT_SWITCH, user!!.username)
        }
    }

    fun onActionDispatchAcknowledged() {
        actionDispatch.value = null
    }

    enum class BottomNavigationDrawerAction {
        NOTIFY_MAIN_ACTIVITY_FOR_ACCOUNT_SWITCH,
        NOTIFY_MAIN_ACTIVITY_FOR_ADD_ACCOUNT
    }
}
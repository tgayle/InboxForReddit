package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.MessageRepository
import app.tgayle.inboxforreddit.db.repository.UserRepository
import app.tgayle.inboxforreddit.util.default

class MainActivityViewModel(val messageRepository: MessageRepository, val userRepository: UserRepository): ViewModel(), MainActivityModel {
    var nightModeEnabled = userRepository.nightModeActive
    private set

    private val currentToolbarText = MutableLiveData<String>()
    private val toolbarScrollEnabled = MutableLiveData<Boolean>().default(true)
    private val actionDispatch = MutableLiveData<MainActivityState>()

    override fun onIntentOccurred(intent: Intent) {
    }

    fun getCurrentToolbarText(): LiveData<String> = currentToolbarText
    fun getToolbarScrollEnabled(): LiveData<Boolean> = toolbarScrollEnabled
    fun getActionDispatch(): LiveData<MainActivityState> = actionDispatch

    override fun requestToolbarTitleChange(title: String?) {
        if (title != null) {
            currentToolbarText.value = title
        }
    }

    override fun requestChangeToolbarScrollState(enabled: Boolean) {
        toolbarScrollEnabled.value = enabled
        Log.d("MainActivityViewModel", "Toolbar scroll to be set to $enabled")
    }

    fun onActionAcknowledged() {
        actionDispatch.value = MainActivityState.EmptyState
    }

    override fun requestNavigateAddAccount() {
        actionDispatch.value = MainActivityState.NavigateLogin
    }

    fun themeChangeRequested() {
        userRepository.nightModeActive = !nightModeEnabled
        nightModeEnabled = userRepository.nightModeActive
        actionDispatch.value = MainActivityState.RecreateActivity
    }

    fun requestUpdateToolbarVisibility(visible: Boolean) {
        actionDispatch.value = MainActivityState.UpdateToolbarVisibility(visible)
    }
}
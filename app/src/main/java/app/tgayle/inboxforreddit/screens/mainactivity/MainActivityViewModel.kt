package app.tgayle.inboxforreddit.screens.mainactivity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.tgayle.inboxforreddit.db.repository.DataRepository
import app.tgayle.inboxforreddit.util.default

class MainActivityViewModel(val dataRepository: DataRepository): ViewModel(), MainActivityModel {
    private val currentToolbarText = MutableLiveData<String>()
    private val toolbarScrollEnabled = MutableLiveData<Boolean>().default(true)
    private val navigationDecision = MutableLiveData<MainActivityAction?>()

    enum class MainActivityAction {
        NAVIGATE_LOGIN
    }

    override fun onIntentOccurred(intent: Intent) {
    }

    fun getCurrentToolbarText(): LiveData<String> = currentToolbarText
    fun getToolbarScrollEnabled(): LiveData<Boolean> = toolbarScrollEnabled
    fun getNavigationDecision(): LiveData<MainActivityAction?> = navigationDecision

    override fun requestToolbarTitleChange(title: String?) {
        if (title != null) {
            currentToolbarText.value = title
        }
    }

    override fun requestChangeToolbarScrollState(enabled: Boolean) {
        toolbarScrollEnabled.value = enabled
        Log.d("MainActivityViewModel", "Toolbar scroll to be set to $enabled")
    }

    fun onNavigationAcknowledged() {
        navigationDecision.value = null
    }

    override fun requestNavigateAddAccount() {
        navigationDecision.value = MainActivityAction.NAVIGATE_LOGIN
    }
}
package app.tgayle.inboxforreddit.db.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import app.tgayle.inboxforreddit.db.AppDatabase
import app.tgayle.inboxforreddit.di.InboxApplicationScope
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditClientAccountPair
import kotlinx.coroutines.*
import net.dean.jraw.RedditClient
import net.dean.jraw.oauth.AccountHelper
import javax.inject.Inject

@InboxApplicationScope
class UserRepository @Inject constructor(private val appDatabase: AppDatabase,
                                         private val accountHelper: AccountHelper,
                                         private val sharedPreferences: SharedPreferences) {
    private val redditClient = MutableLiveData<RedditClientAccountPair>()
    var nightModeActive: Boolean = false
    set(value) {
        sharedPreferences.edit(commit = true) {
            putBoolean(SHARED_PREF_CURRENT_THEME, value)
        }
        Log.d("User Repo", "Night theme enabled set to ${sharedPreferences.getBoolean(SHARED_PREF_CURRENT_THEME, false)}")
        field = value
    }

    /*
    Load shared preferences user on initialization.
     */
    init {
        nightModeActive = sharedPreferences.getBoolean(SHARED_PREF_CURRENT_THEME, false)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val client = getClientFromUser(sharedPreferences.getString(CURRENT_USER, null)).await()
                redditClient.postValue(client)
            }
        }
    }

    fun getCurrentRedditUser(): LiveData<RedditClientAccountPair> = redditClient

    fun getUsers() = appDatabase.accounts().getAllUsers()

    fun getUsersDeferred() = GlobalScope.async(Dispatchers.IO ) {
        return@async appDatabase.accounts().getAllSync()
    }

    /**
     * Retrieves relevant user information from Reddit then saves the user locally. Runs asynchronously.
     * @param user A JRAW client containing user information.
     */
    fun saveUser(user: RedditClient) = GlobalScope.async {
        val account = user.me().query().account
        if (account != null) {
            appDatabase.accounts().saveUser(RedditAccount(account.uniqueId, account.name, account.created, user.authManager.refreshToken!!))
            return@async appDatabase.accounts().getUserSync(account.name)!!
        } else {
            throw RuntimeException("Tried to save a user but account was null: ${user.requireAuthenticatedUser()}")
        }
    }

    /**
     * Updates the repository's current user.
     * @param newUser
     */
    fun updateCurrentUser(newUser: RedditClientAccountPair, updateSharedPreferences: Boolean = true) {
        redditClient.postValue(newUser)
        if (updateSharedPreferences) {
            sharedPreferences.edit(commit = true) {
                putString(CURRENT_USER, newUser.account?.name)
                Log.d("User Repo", "Current user updated to ${newUser.account?.name}")
            }
        }
    }

    /**
     * Retrieves local user information and returns it along with a client for making requests with that user.
     * @param name The name of the user to retrive a client for.
     */
    fun getClientFromUser(name: String?) = GlobalScope.async {
        val client = if (name == null) null else accountHelper.switchToUser(name)
        return@async RedditClientAccountPair(client, appDatabase.accounts().getUserSync(name))
    }

    /**
     * @see getClientFromUser(String)
     */
    fun getClientFromUser(user: RedditAccount) = getClientFromUser(user.name)


    fun getUsersAndUnreadMessageCountForEach() = LivePagedListBuilder(appDatabase.messages().getUsernamesAndUnreadMessageCountsForEach(), 5).build()

    /**
     * Returns a [RedditAccount] from the local database or null if the user doesn't exist. Runs synchronously and must
     * be run on another thread.
     * @param username The name of the user to retrieve.
     */
    fun getUserSync(username: String?): RedditAccount? = appDatabase.accounts().getUserSync(username)

    /**
     * Removes a given user from the local database and all their messages.
     * @param username The name of the user to be removed.
     */
    fun removeUser(username: String?) = GlobalScope.async {
        if (username == null) return@async
        appDatabase.accounts().removeUserByName(username)
        appDatabase.messages().removeMessagesWithOwner(username)
    }

    /**
     * Returns the currentUser from shared preferences, or null if none is set.
     */
    fun getSharedPreferencesCurrentUser(): String? = sharedPreferences.getString(CURRENT_USER, null)

    companion object {
        const val CURRENT_USER = "currentUser"
        const val SHARED_PREF_CURRENT_THEME = "nightModeActive"
    }
}
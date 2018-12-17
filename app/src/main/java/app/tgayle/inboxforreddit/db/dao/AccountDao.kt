package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.tgayle.inboxforreddit.model.RedditAccount

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAllSync(): List<RedditAccount>

    @Query("SELECT * FROM accounts WHERE name = :name")
    fun getUserSync(name: String?): RedditAccount?

    @Query("UPDATE accounts SET refreshToken = :refreshToken WHERE name = :username")
    fun updateUserAuthData(username: String, refreshToken: String?)

    @Query("SELECT * FROM accounts")
    fun getAllUsers(): LiveData<List<RedditAccount>?>

    @Query("SELECT * FROM accounts WHERE name = :name")
    fun getUser(name: String): LiveData<RedditAccount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(accounts: RedditAccount): Long

    @Delete
    fun removeUser(user: RedditAccount?): Int

    @Query("DELETE FROM accounts WHERE name = :username")
    fun removeUserByName(username: String?)
}
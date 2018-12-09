package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.tgayle.inboxforreddit.model.RedditAccount

@Dao
interface AccountDao {
    @Query("SELECT * FROM RedditAccount")
    fun getAllSync(): List<RedditAccount>

    @Query("UPDATE RedditAccount SET refreshToken = :refreshToken WHERE name = :username")
    fun updateUserAuthData(username: String, refreshToken: String?)

    @Query("SELECT * FROM RedditAccount")
    fun getAllUsers(): LiveData<List<RedditAccount>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(redditAccount: RedditAccount): Long
}
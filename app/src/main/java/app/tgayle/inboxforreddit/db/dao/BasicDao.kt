package app.tgayle.inboxforreddit.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import app.tgayle.inboxforreddit.model.TestItem

@Dao
interface BasicDao {
    @Query("SELECT * FROM TestItem")
    fun getAll(): LiveData<List<TestItem>>
}
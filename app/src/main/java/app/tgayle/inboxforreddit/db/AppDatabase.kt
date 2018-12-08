package app.tgayle.inboxforreddit.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.tgayle.inboxforreddit.db.dao.BasicDao
import app.tgayle.inboxforreddit.model.TestItem

@Database(entities = [TestItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dao(): BasicDao

}
package app.tgayle.inboxforreddit.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import app.tgayle.inboxforreddit.db.dao.AccountDao
import app.tgayle.inboxforreddit.db.dao.MessageDao
import app.tgayle.inboxforreddit.model.RedditAccount
import app.tgayle.inboxforreddit.model.RedditMessage
import app.tgayle.inboxforreddit.model.RedditMessage.DistinguishedState.*
import java.util.*

@Database(entities = [RedditAccount::class, RedditMessage::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun accounts(): AccountDao
    abstract fun messages(): MessageDao

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromUUID(value: String) = UUID.fromString(value)

    @TypeConverter
    fun uuidToString(uuid: UUID) = uuid.toString()

    @TypeConverter
    fun userToName(user: RedditAccount) = user.name

    @TypeConverter
    fun distinguishedToInt(state: RedditMessage.DistinguishedState) = when (state) {
        NORMAL -> 1
        MODERATOR -> 2
        ADMIN -> 3
        GOLD -> 4
        SPECIAL -> 5
    }

    @TypeConverter
    fun intToDistinguished(num: Int): RedditMessage.DistinguishedState = when (num) {
        1 -> NORMAL
        2 -> MODERATOR
        3 -> ADMIN
        4 -> GOLD
        5 -> SPECIAL
        else -> NORMAL
    }
}
package app.echoirx.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.echoirx.data.local.converter.Converters
import app.echoirx.data.local.dao.DownloadDao
import app.echoirx.domain.model.Download

@Database(
    entities = [Download::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        const val DATABASE_NAME = "echoir_db"
    }
}
package app.echoirx.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.echoirx.data.local.converter.Converters
import app.echoirx.data.local.dao.DownloadDao
import app.echoirx.data.local.dao.SearchHistoryDao
import app.echoirx.domain.model.Download
import app.echoirx.domain.model.SearchHistory

@Database(
    entities = [
        Download::class,
        SearchHistory::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val DATABASE_NAME = "echoir_db"
    }
}
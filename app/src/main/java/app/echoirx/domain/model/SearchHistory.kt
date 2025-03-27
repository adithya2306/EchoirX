package app.echoirx.domain.model

import androidx.room.Entity
import app.echoirx.presentation.screens.search.SearchType

@Entity(
    tableName = "search_history",
    primaryKeys = ["query", "type"]
)
data class SearchHistory(
    val query: String,
    val type: SearchType,
    val timestamp: Long = System.currentTimeMillis()
)

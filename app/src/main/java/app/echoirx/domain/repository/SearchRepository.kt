package app.echoirx.domain.repository

import app.echoirx.domain.model.SearchHistory
import app.echoirx.domain.model.SearchResult
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(query: String, type: SearchType): List<SearchResult>
    suspend fun getAlbumTracks(albumId: Long): List<SearchResult>
    suspend fun filterSearchResults(
        results: List<SearchResult>,
        filter: SearchFilter
    ): List<SearchResult>
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun deleteSearchHistoryItem(item: SearchHistory)
}
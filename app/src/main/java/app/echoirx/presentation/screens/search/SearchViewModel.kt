package app.echoirx.presentation.screens.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.echoirx.R
import app.echoirx.domain.model.DownloadRequest
import app.echoirx.domain.model.QualityConfig
import app.echoirx.domain.model.SearchHistory
import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.usecase.ProcessDownloadUseCase
import app.echoirx.domain.usecase.SearchUseCase
import app.echoirx.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val processDownloadUseCase: ProcessDownloadUseCase,
    private val settingsUseCase: SettingsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsUseCase.getShowUnsupportedFormatsAsFlow()
                .collect { showUnsupportedFormats ->
                    _state.update {
                        it.copy(
                            showUnsupportedFormats = showUnsupportedFormats,
                            availableQualities = SearchQuality.entries
                                .filter { q -> showUnsupportedFormats || q.isSupported }
                        )
                            .apply {
                                // Remove previously applied filter that may be unsupported now.
                                if (!showUnsupportedFormats) {
                                    searchFilter.qualities.removeIf { q -> !q.isSupported }
                                }
                            }
                    }
                    onSearchFilterChanged()
                }
        }
    }

    val searchHistory: StateFlow<List<SearchHistory>> = searchUseCase.getSearchHistory()
        .combine(state) {
            searchHistory, state ->
                searchHistory.filter { it.type == state.searchType }
                    .take(MAX_SEARCH_HISTORY)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onQueryChange(query: String) {
        _state.update {
            it.copy(
                query = query,
                status = when {
                    query.isEmpty() -> SearchStatus.Empty
                    else -> SearchStatus.Ready
                }
            )
        }
    }

    fun onSearchTypeChange(type: SearchType) {
        _state.update {
            it.copy(
                searchType = type
            )
        }
        if (_state.value.query.isNotEmpty()) {
            search()
        }
    }

    fun onSearchFilterQualityAdded(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.add(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchFilterQualityRemoved(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.remove(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterAdded(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.add(contentFilter) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterRemoved(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.remove(contentFilter) }
        }
        onSearchFilterChanged()
    }

    private fun onSearchFilterChanged() {
        if (_state.value.results.isNotEmpty()) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        filteredResults = searchUseCase.filterSearchResults(
                            _state.value.results,
                            _state.value.searchFilter,
                            _state.value.showUnsupportedFormats
                        )
                    )
                }
            }
        }
    }

    fun search() {
        val currentState = _state.value

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        status = SearchStatus.Loading
                    )
                }

                val serverUrl = settingsUseCase.getServerUrl()

                // Check if using example server before attempting network request
                if (serverUrl.contains("example.com")) {
                    _state.update {
                        it.copy(
                            error = context.getString(R.string.error_example_server),
                            status = SearchStatus.Error,
                            showServerRecommendation = true
                        )
                    }
                    return@launch
                }

                val results = when (currentState.searchType) {
                    SearchType.TRACKS -> searchUseCase.searchTracks(currentState.query)
                    SearchType.ALBUMS -> searchUseCase.searchAlbums(currentState.query)
                }

                _state.update {
                    it.copy(
                        results = results,
                        filteredResults = searchUseCase.filterSearchResults(
                            results,
                            _state.value.searchFilter,
                            _state.value.showUnsupportedFormats
                        ),
                        status = if (results.isEmpty()) SearchStatus.NoResults else SearchStatus.Success,
                        showServerRecommendation = false
                    )
                }
            } catch (e: Exception) {
                // Check if the error might be related to the example server
                val serverUrl = settingsUseCase.getServerUrl()
                val isExampleServer = serverUrl.contains("example.com") ||
                        e.message?.contains("example.com") == true

                _state.update {
                    it.copy(
                        error = if (isExampleServer)
                            context.getString(R.string.error_example_server)
                        else
                            e.message,
                        status = SearchStatus.Error,
                        showServerRecommendation = isExampleServer
                    )
                }
            }
        }
    }

    fun downloadTrack(track: SearchResult, config: QualityConfig) {
        viewModelScope.launch {
            processDownloadUseCase(
                DownloadRequest.Track(
                    track = track,
                    config = config
                )
            )
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                query = "",
                results = emptyList(),
                filteredResults = emptyList(),
                error = null,
                status = SearchStatus.Empty,
                showServerRecommendation = false
            )
        }
    }

    fun deleteSearchHistoryItem(item: SearchHistory) =
        viewModelScope.launch {
            searchUseCase.deleteSearchHistoryItem(item)
        }

    companion object {
        const val MAX_SEARCH_HISTORY = 5
    }
}
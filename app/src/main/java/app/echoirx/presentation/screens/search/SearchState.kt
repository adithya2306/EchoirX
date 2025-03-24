package app.echoirx.presentation.screens.search

import androidx.annotation.StringRes
import app.echoirx.R
import app.echoirx.domain.model.QualityConfig
import app.echoirx.domain.model.SearchResult

data class SearchState(
    val query: String = "",
    val searchType: SearchType = SearchType.TRACKS,
    val searchFilter: SearchFilter = SearchFilter(),
    val results: List<SearchResult> = emptyList(),
    val filteredResults: List<SearchResult> = emptyList(),
    val status: SearchStatus = SearchStatus.Empty,
    val error: String? = null,
    val showServerRecommendation: Boolean = false,
    val showUnsupportedFormats: Boolean = false,
)

enum class SearchType(
    @StringRes val title: Int
) {
    TRACKS(R.string.label_tracks),
    ALBUMS(R.string.label_albums)
}

data class SearchFilter(
    val qualities: MutableList<SearchQuality> = mutableListOf(),
    var contentFilters: MutableList<SearchContentFilter> = mutableListOf()
)

enum class SearchContentFilter(
    @StringRes val label: Int,
    val explicit: Boolean
) {
    CLEAN(R.string.label_clean, false),
    EXPLICIT(R.string.label_explicit, true),
}

enum class SearchQuality(
    @StringRes val label: Int,
    val format: String,
    val isSupported: Boolean = true
) {
    HIRES(
        R.string.quality_label_hires,
        "HIRES_LOSSLESS"
    ),
    LOSSLESS(
        R.string.quality_label_lossless,
        "LOSSLESS"
    ),
    ATMOS(R.string.label_dolby_atmos,
        "DOLBY_ATMOS",
        QualityConfig.DolbyAtmosAC3.isSupported() ||
            QualityConfig.DolbyAtmosAC4.isSupported()
    ),
}

sealed class SearchStatus {
    data object Empty : SearchStatus()
    data object Ready : SearchStatus()
    data object Loading : SearchStatus()
    data object Success : SearchStatus()
    data object NoResults : SearchStatus()
    data object Error : SearchStatus()
}
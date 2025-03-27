package app.echoirx.domain.model

import android.os.Parcelable
import app.echoirx.presentation.screens.search.SearchQuality
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    val id: Long,
    val title: String,
    val duration: String,
    val explicit: Boolean,
    val cover: String?,
    val artists: List<String>,
    val modes: List<String>?,
    val formats: List<String>?
) : Parcelable {
    // Whether the search result has atleast one supported format.
    fun hasSupportedFormat(): Boolean =
        formats?.any { format ->
            SearchQuality.entries
                .find { it.format == format }
                ?.isSupported ?: false
        } ?: false
}
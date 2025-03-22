package app.echoirx.domain.model

import androidx.annotation.StringRes
import app.echoirx.R

enum class FileNamingFormat(
    @StringRes val displayNameResId: Int,
    val previewText: String,
    val format: (String, String) -> String
) {
    ARTIST_TITLE(
        R.string.file_format_artist_title_display,
        "Artist - Title.mp3",
        { artist, title -> "${artist.split(",").first().trim()} - $title" }
    ),
    TITLE_ARTIST(
        R.string.file_format_title_artist_display,
        "Title - Artist.mp3",
        { artist, title -> "$title - ${artist.split(",").first().trim()}" }
    ),
    TITLE_ONLY(
        R.string.file_format_title_only_display,
        "Title.mp3",
        { _, title -> title }
    );

    companion object {
        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: TITLE_ONLY
    }
}
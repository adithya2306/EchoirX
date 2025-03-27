package app.echoirx.presentation.components

import app.echoirx.domain.model.QualityConfig

data class DownloadOptionsState(
    val options: List<QualityConfig> = emptyList(),
    val showUnsupportedFormats: Boolean = false
)

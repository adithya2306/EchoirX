package app.echoirx.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.echoirx.domain.model.QualityConfig
import app.echoirx.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadOptionsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
): ViewModel() {
    private val _state = MutableStateFlow(DownloadOptionsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsUseCase.getShowUnsupportedFormatsAsFlow()
                .collect { showUnsupportedFormats ->
                    _state.update {
                        it.copy(
                            showUnsupportedFormats = showUnsupportedFormats
                        )
                    }
                }
        }
    }

    fun updateDownloadOptions(modes: List<String>?, formats: List<String>?) {
        _state.update {
            it.copy(
                options = buildList {
                    if (!formats.isNullOrEmpty() && !modes.isNullOrEmpty()) {
                        val hasDolbyAtmos = modes.contains("DOLBY_ATMOS")
                                && formats.contains("DOLBY_ATMOS")
                        val hasStereo = modes.contains("STEREO")

                        if (hasDolbyAtmos) {
                            add(QualityConfig.DolbyAtmosAC3)
                            add(QualityConfig.DolbyAtmosAC4)
                        }

                        if (hasStereo && formats.contains("HIRES_LOSSLESS")) {
                            add(QualityConfig.HiRes)
                        }

                        if (hasStereo && !hasDolbyAtmos) {
                            if (formats.contains("LOSSLESS")) {
                                add(QualityConfig.Lossless)
                            }
                            add(QualityConfig.AAC320)
                            add(QualityConfig.AAC96)
                        }
                    }
                }
            )
        }
    }
}
package app.echoirx.presentation.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import app.echoirx.data.local.dao.DownloadDao
import app.echoirx.data.local.dao.SearchHistoryDao
import app.echoirx.domain.model.FileNamingFormat
import app.echoirx.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val workManager: WorkManager,
    private val downloadDao: DownloadDao,
    private val searchHistoryDao: SearchHistoryDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val defaultServerUrl = "https://example.com/api/echoir"

    init {
        viewModelScope.launch {
            val dir = settingsUseCase.getOutputDirectory()
            val format = settingsUseCase.getFileNamingFormat()
            val region = settingsUseCase.getRegion()
            val serverUrl = settingsUseCase.getServerUrl()
            val showUnsupportedFormats = settingsUseCase.getShowUnsupportedFormats()

            _state.update {
                it.copy(
                    outputDirectory = dir,
                    fileNamingFormat = format,
                    region = region,
                    serverUrl = serverUrl,
                    showUnsupportedFormats = showUnsupportedFormats
                )
            }
        }
    }

    fun updateOutputDirectory(uri: String) {
        viewModelScope.launch {
            settingsUseCase.setOutputDirectory(uri)
            _state.update {
                it.copy(
                    outputDirectory = uri
                )
            }
        }
    }

    fun updateFileNamingFormat(format: FileNamingFormat) {
        viewModelScope.launch {
            settingsUseCase.setFileNamingFormat(format)
            _state.update {
                it.copy(
                    fileNamingFormat = format
                )
            }
        }
    }

    fun updateRegion(region: String) {
        viewModelScope.launch {
            settingsUseCase.setRegion(region)
            _state.update {
                it.copy(
                    region = region
                )
            }
        }
    }

    fun updateServerUrl(url: String) {
        if (url.isBlank()) return

        viewModelScope.launch {
            settingsUseCase.setServerUrl(url)
            _state.update {
                it.copy(
                    serverUrl = url
                )
            }
        }
    }

    fun updateShowUnsupportedFormats(show: Boolean) {
        viewModelScope.launch {
            settingsUseCase.setShowUnsupportedFormats(show)
            _state.update {
                it.copy(
                    showUnsupportedFormats = show
                )
            }
        }
    }

    fun resetServerSettings() {
        viewModelScope.launch {
            settingsUseCase.resetServerSettings()
            _state.update {
                it.copy(
                    serverUrl = defaultServerUrl
                )
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            workManager.cancelAllWork()
            downloadDao.deleteAll()
            searchHistoryDao.deleteAll()
            context.cacheDir.deleteRecursively()
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsUseCase.setOutputDirectory(null)
            settingsUseCase.setFileNamingFormat(FileNamingFormat.TITLE_ONLY)
            settingsUseCase.setRegion("BR")
            settingsUseCase.resetServerSettings()

            _state.update {
                it.copy(
                    outputDirectory = null,
                    fileNamingFormat = FileNamingFormat.TITLE_ONLY,
                    region = "BR",
                    serverUrl = defaultServerUrl
                )
            }
        }
    }
}
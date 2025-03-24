package app.echoirx.domain.usecase

import app.echoirx.domain.model.FileNamingFormat
import app.echoirx.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun getOutputDirectory(): String? = repository.getOutputDirectory()

    suspend fun setOutputDirectory(uri: String?) = repository.setOutputDirectory(uri)

    suspend fun getFileNamingFormat(): FileNamingFormat = repository.getFileNamingFormat()

    suspend fun setFileNamingFormat(format: FileNamingFormat) =
        repository.setFileNamingFormat(format)

    suspend fun getRegion(): String = repository.getRegion()

    suspend fun setRegion(region: String) = repository.setRegion(region)

    suspend fun getServerUrl(): String = repository.getServerUrl()

    suspend fun setServerUrl(url: String) = repository.setServerUrl(url)

    suspend fun resetServerSettings() {
        repository.setServerUrl("https://example.com/api/echoir")
    }

    suspend fun getShowUnsupportedFormats() =
        repository.getShowUnsupportedFormats().first()

    fun getShowUnsupportedFormatsAsFlow() = repository.getShowUnsupportedFormats()

    suspend fun setShowUnsupportedFormats(show: Boolean) =
        repository.setShowUnsupportedFormats(show)
}
package app.echoirx.domain.repository

import app.echoirx.domain.model.FileNamingFormat
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getOutputDirectory(): String?
    suspend fun setOutputDirectory(uri: String?)
    suspend fun getFileNamingFormat(): FileNamingFormat
    suspend fun setFileNamingFormat(format: FileNamingFormat)
    suspend fun getRegion(): String
    suspend fun setRegion(region: String)
    suspend fun getServerUrl(): String
    suspend fun setServerUrl(url: String)
    fun getShowUnsupportedFormats(): Flow<Boolean>
    suspend fun setShowUnsupportedFormats(show: Boolean)
}
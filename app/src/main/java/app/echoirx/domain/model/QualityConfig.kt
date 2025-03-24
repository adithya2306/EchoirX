package app.echoirx.domain.model

import android.media.MediaCodecList
import android.media.MediaCodecList.ALL_CODECS
import androidx.annotation.StringRes
import app.echoirx.R

sealed class QualityConfig(
    @StringRes val label: Int,
    @StringRes val shortLabel: Int = label,
    val quality: String,
    val ac4: Boolean = false,
    val immersive: Boolean = true,
    @StringRes val summary: Int
) {
    data object HiRes : QualityConfig(
        label = R.string.quality_label_hires,
        shortLabel = R.string.quality_label_hires_short,
        quality = "HI_RES_LOSSLESS",
        summary = R.string.quality_desc_hires
    )

    data object Lossless : QualityConfig(
        label = R.string.quality_label_lossless,
        shortLabel = R.string.quality_label_lossless_short,
        quality = "LOSSLESS",
        summary = R.string.quality_desc_lossless
    )

    data object AAC320 : QualityConfig(
        label = R.string.quality_label_aac_320,
        shortLabel = R.string.quality_label_aac_320_short,
        quality = "HIGH",
        summary = R.string.quality_desc_aac_320
    )

    data object AAC96 : QualityConfig(
        label = R.string.quality_label_aac_96,
        shortLabel = R.string.quality_label_aac_96_short,
        quality = "LOW",
        summary = R.string.quality_desc_aac_96
    )

    data object DolbyAtmosAC3 : QualityConfig(
        label = R.string.quality_label_dolby_ac3,
        shortLabel = R.string.quality_label_dolby_ac3_short,
        quality = "DOLBY_ATMOS",
        summary = R.string.quality_desc_dolby_ac3
    )

    data object DolbyAtmosAC4 : QualityConfig(
        label = R.string.quality_label_dolby_ac4,
        shortLabel = R.string.quality_label_dolby_ac4_short,
        quality = "DOLBY_ATMOS",
        ac4 = true,
        summary = R.string.quality_desc_dolby_ac4
    )

    fun isSupported(): Boolean =
        when (this) {
            DolbyAtmosAC3 -> isDecoderPresent(MIMETYPE_AUDIO_EAC3_JOC)
            DolbyAtmosAC4 -> isDecoderPresent(MIMETYPE_AUDIO_AC4)
            else -> true
        }

    private fun isDecoderPresent(mimeType: String): Boolean =
        MediaCodecList(ALL_CODECS)
            .codecInfos
            .any { codecInfo ->
                !codecInfo.isEncoder && codecInfo.supportedTypes.contains(mimeType)
            }

    companion object {
        private const val MIMETYPE_AUDIO_EAC3_JOC = "audio/eac3-joc"
        private const val MIMETYPE_AUDIO_AC4 = "audio/ac4"
    }
}
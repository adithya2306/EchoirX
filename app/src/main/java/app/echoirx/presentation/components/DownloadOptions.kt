package app.echoirx.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.echoirx.R
import app.echoirx.domain.model.QualityConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadOptions(
    formats: List<String>?,
    modes: List<String>?,
    onOptionSelected: (QualityConfig) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadOptionsViewModel = hiltViewModel(),
    onOptionsRemoved : (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    val options = state.options
        .filter { state.showUnsupportedFormats || it.isSupported() }

    if (onOptionsRemoved != null) {
        LaunchedEffect(options) {
            if (state.options.isNotEmpty() && options.isEmpty()) {
                onOptionsRemoved()
                return@LaunchedEffect
            }
        }
    }

    LaunchedEffect(modes, formats) {
        viewModel.updateDownloadOptions(modes, formats)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            options.forEach { config ->
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(config.summary)
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { onOptionSelected(config) },
                        label = {
                            Text(
                                text = stringResource(config.shortLabel),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_download),
                                contentDescription = stringResource(
                                    R.string.cd_download_option,
                                    config.shortLabel
                                ),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            iconColor = MaterialTheme.colorScheme.onSurface,
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            enabled = true,
                            selected = false
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
    }
}
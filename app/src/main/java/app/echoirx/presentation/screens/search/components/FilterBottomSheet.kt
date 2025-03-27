package app.echoirx.presentation.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.echoirx.R
import app.echoirx.presentation.screens.search.SearchContentFilter
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchQuality

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: SearchFilter,
    searchQualities: List<SearchQuality> = SearchQuality.entries,
    onQualityFilterAdded: (SearchQuality) -> Unit,
    onQualityFilterRemoved: (SearchQuality) -> Unit,
    onContentFilterAdded: (SearchContentFilter) -> Unit,
    onContentFilterRemoved: (SearchContentFilter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.small,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.title_filter_options),
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_quality),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchQualities.forEach { quality ->
                        var selected by remember {
                            mutableStateOf(currentFilter.qualities.any { it == quality })
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    onQualityFilterAdded(quality)
                                } else {
                                    onQualityFilterRemoved(quality)
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(quality.label),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_content),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchContentFilter.entries.forEach { contentFilter ->
                        var selected by remember {
                            mutableStateOf(currentFilter.contentFilters.any { it == contentFilter })
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    onContentFilterAdded(contentFilter)
                                } else {
                                    onContentFilterRemoved(contentFilter)
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(contentFilter.label),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}
package app.echoirx.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Explicit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.echoirx.R
import app.echoirx.data.utils.extensions.formatErrorMessage
import app.echoirx.data.utils.extensions.showSnackbar
import app.echoirx.domain.model.SearchResult
import app.echoirx.presentation.components.EmptyStateMessage
import app.echoirx.presentation.components.TrackBottomSheet
import app.echoirx.presentation.navigation.Route
import app.echoirx.presentation.screens.search.components.SearchHistoryItem
import app.echoirx.presentation.screens.search.components.SearchResultItem

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val searchHistory by viewModel.searchHistory.collectAsState()

    var selectedTrack by remember { mutableStateOf<SearchResult?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFilterColumn by remember { mutableStateOf(false) }

    // hide filter chips when we start scrolling search results
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { scrollOffset ->
                if (scrollOffset > 0) {
                    showFilterColumn = false
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = MaterialTheme.shapes.small,
                placeholder = {
                    Text(
                        text = stringResource(
                            R.string.hint_search,
                            stringResource(state.searchType.title)
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.cd_search),
                    )
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearSearch() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = stringResource(R.string.cd_clear_search)
                            )
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (state.status == SearchStatus.Ready) {
                            viewModel.search()
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchType.entries.forEach { type ->
                    FilterChip(
                        selected = state.searchType == type,
                        onClick = {
                            viewModel.onSearchTypeChange(type)
                            focusManager.clearFocus()
                        },
                        label = {
                            Text(
                                text = stringResource(type.title),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = state.searchType == type,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(R.string.tooltip_filter)
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(
                        onClick = { showFilterColumn = !showFilterColumn }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = stringResource(R.string.cd_filter_button)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFilterColumn,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AudioFile,
                        contentDescription = stringResource(R.string.cd_filter_quality),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.title_quality),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchQuality.entries.forEach { quality ->
                        var selected by remember {
                            mutableStateOf(state.searchFilter.qualities.contains(quality))
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    viewModel.onSearchFilterQualityAdded(quality)
                                } else {
                                    viewModel.onSearchFilterQualityRemoved(quality)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explicit,
                        contentDescription = stringResource(R.string.cd_filter_content),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.title_content),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchContentFilter.entries.forEach { contentFilter ->
                        var selected by remember {
                            mutableStateOf(
                                state.searchFilter.contentFilters.contains(contentFilter)
                            )
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    viewModel.onSearchContentFilterAdded(contentFilter)
                                } else {
                                    viewModel.onSearchContentFilterRemoved(contentFilter)
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

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        when (state.status) {
            SearchStatus.Empty, SearchStatus.Ready -> {
                if (searchHistory.isEmpty()) {
                    EmptyStateMessage(
                        title = stringResource(R.string.msg_search_empty),
                        description = stringResource(R.string.msg_search_empty_desc),
                        painter = painterResource(R.drawable.ic_search)
                    )
                } else {
                    LazyColumn {
                        items(searchHistory) { item ->
                            SearchHistoryItem(
                                item = item,
                                onClick = {
                                    viewModel.onQueryChange(item.query)
                                    viewModel.search()
                                },
                                onDelete = { viewModel.deleteSearchHistoryItem(it) }
                            )
                        }
                    }
                }
            }

            SearchStatus.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            SearchStatus.Success -> {
                if (state.filteredResults.isNotEmpty()) {
                    LazyColumn(
                        state = lazyListState
                    ) {
                        items(state.filteredResults) { result ->
                            SearchResultItem(
                                result = result,
                                onClick = {
                                    if (state.searchType == SearchType.TRACKS) {
                                        selectedTrack = result
                                        showBottomSheet = true
                                    } else {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("result", result)
                                        navController.navigate(
                                            Route.Search.Details().createPath(
                                                type = state.searchType.name,
                                                id = result.id
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                } else {
                    EmptyStateMessage(
                        title = stringResource(R.string.msg_search_no_results_filters),
                        description = stringResource(R.string.msg_search_no_results_filters_desc),
                        painter = painterResource(R.drawable.ic_search)
                    )
                }
            }

            SearchStatus.NoResults -> {
                EmptyStateMessage(
                    title = stringResource(R.string.msg_search_no_results),
                    description = stringResource(R.string.msg_search_no_results_desc),
                    painter = painterResource(R.drawable.ic_search)
                )
            }

            SearchStatus.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.showServerRecommendation) {
                        EmptyStateMessage(
                            title = stringResource(R.string.title_server_recommendation),
                            description = stringResource(R.string.msg_server_recommendation),
                            icon = Icons.Outlined.CloudOff
                        )
                    } else {
                        EmptyStateMessage(
                            title = stringResource(R.string.msg_unknown_error),
                            description = state.error.formatErrorMessage(
                                defaultError = stringResource(R.string.msg_unknown_error)
                            ),
                            icon = Icons.Outlined.Error,
                            buttonText = stringResource(R.string.action_retry),
                            onButtonClick = { viewModel.search() }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedTrack != null) {
        TrackBottomSheet(
            track = selectedTrack!!,
            onDownload = { config ->
                viewModel.downloadTrack(selectedTrack!!, config)
                snackbarHostState.showSnackbar(
                    scope = coroutineScope,
                    message = context.getString(
                        R.string.msg_download_started,
                        context.getString(config.label)
                    )
                )
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}
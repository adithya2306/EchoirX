package app.echoirx.presentation.screens.settings

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.echoirx.BuildConfig
import app.echoirx.R
import app.echoirx.data.utils.extensions.toDisplayPath
import app.echoirx.domain.model.Region
import app.echoirx.presentation.components.preferences.PreferenceCategory
import app.echoirx.presentation.components.preferences.PreferenceItem
import app.echoirx.presentation.components.preferences.PreferencePosition
import app.echoirx.presentation.screens.settings.components.FileNamingBottomSheet
import app.echoirx.presentation.screens.settings.components.RegionBottomSheet
import app.echoirx.presentation.screens.settings.components.ServerBottomSheet
import app.echoirx.presentation.screens.settings.components.SettingsActionBottomSheet

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()

    var showFormatSheet by remember { mutableStateOf(false) }
    var showResetSheet by remember { mutableStateOf(false) }
    var showClearDataSheet by remember { mutableStateOf(false) }
    var showRegionSheet by remember { mutableStateOf(false) }
    var showServerSheet by remember { mutableStateOf(false) }

    val dirPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.updateOutputDirectory(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dirPicker.launch(null)
        }
    }

    if (showFormatSheet) {
        FileNamingBottomSheet(
            selectedFormat = state.fileNamingFormat,
            onSelectFormat = { format ->
                viewModel.updateFileNamingFormat(format)
            },
            onDismiss = { showFormatSheet = false }
        )
    }

    if (showResetSheet) {
        SettingsActionBottomSheet(
            title = stringResource(R.string.dialog_reset_settings_title),
            description = stringResource(R.string.dialog_reset_settings_message),
            icon = Icons.Outlined.RestartAlt,
            confirmText = stringResource(R.string.action_reset),
            cancelText = stringResource(R.string.action_cancel),
            onConfirm = {
                viewModel.resetSettings()
            },
            onDismiss = { showResetSheet = false }
        )
    }

    if (showClearDataSheet) {
        SettingsActionBottomSheet(
            title = stringResource(R.string.dialog_clear_data_title),
            description = stringResource(R.string.dialog_clear_data_message),
            icon = Icons.Outlined.Delete,
            confirmText = stringResource(R.string.action_clear),
            cancelText = stringResource(R.string.action_cancel),
            onConfirm = {
                viewModel.clearData()
            },
            onDismiss = { showClearDataSheet = false }
        )
    }

    if (showRegionSheet) {
        RegionBottomSheet(
            selectedRegion = state.region,
            onSelectRegion = { region ->
                viewModel.updateRegion(region)
            },
            onDismiss = { showRegionSheet = false }
        )
    }

    if (showServerSheet) {
        ServerBottomSheet(
            currentServer = state.serverUrl,
            onSave = { serverUrl ->
                viewModel.updateServerUrl(serverUrl)
            },
            onReset = {
                viewModel.resetServerSettings()
            },
            onDismiss = { showServerSheet = false },
            focusManager = focusManager
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            PreferenceCategory(title = stringResource(R.string.title_content))
        }

        item {
            val regionName = Region.getDisplayName(Region.fromCode(state.region), context)
            PreferenceItem(
                title = stringResource(R.string.title_region),
                subtitle = "$regionName - ${state.region}",
                icon = Icons.Outlined.Public,
                onClick = { showRegionSheet = true },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_server),
                subtitle = stringResource(R.string.msg_server_subtitle),
                icon = Icons.Outlined.CloudQueue,
                onClick = { showServerSheet = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_storage))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_download_location),
                subtitle = state.outputDirectory.toDisplayPath(context),
                icon = Icons.Outlined.Folder,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        dirPicker.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_file_naming_format),
                subtitle = stringResource(state.fileNamingFormat.displayNameResId),
                icon = Icons.Outlined.TextFormat,
                onClick = { showFormatSheet = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_data))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_clear_data),
                subtitle = stringResource(R.string.msg_clear_data_subtitle),
                icon = Icons.Outlined.Delete,
                onClick = { showClearDataSheet = true },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_reset_settings),
                subtitle = stringResource(R.string.msg_reset_settings_subtitle),
                icon = Icons.Outlined.RestartAlt,
                onClick = { showResetSheet = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_about))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.msg_about_version, BuildConfig.VERSION_NAME),
                icon = Icons.Outlined.Info,
                position = PreferencePosition.Single,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
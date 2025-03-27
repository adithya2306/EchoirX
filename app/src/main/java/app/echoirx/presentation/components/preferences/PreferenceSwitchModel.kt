package app.echoirx.presentation.components.preferences

data class PreferenceSwitchModel(
    val isChecked: Boolean,
    val isCheckable: Boolean = true,
    val onCheckedChange: (Boolean) -> Unit
)

package app.echoirx.presentation.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    title: String,
    subtitle: String? = null,
    icon: Any,
    onClick: (() -> Unit)? = null,
    iconTint: Color = LocalContentColor.current,
    position: PreferencePosition = PreferencePosition.Single,
    switchModel: PreferenceSwitchModel? = null,
) {
    val shape = when (position) {
        PreferencePosition.Single -> RoundedCornerShape(12.dp)
        PreferencePosition.Top, PreferencePosition.TopWithGap -> RoundedCornerShape(
            topStart = 12.dp, topEnd = 12.dp,
            bottomStart = 4.dp, bottomEnd = 4.dp
        )

        PreferencePosition.Bottom, PreferencePosition.BottomWithGap -> RoundedCornerShape(
            topStart = 4.dp, topEnd = 4.dp,
            bottomStart = 12.dp, bottomEnd = 12.dp
        )

        PreferencePosition.Middle -> RoundedCornerShape(4.dp)
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        leadingContent = {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )

                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        trailingContent = {
            switchModel?.let { PreferenceSwitch(it) }
        }
    )
}

@Composable
fun PreferenceSwitch(
    model: PreferenceSwitchModel
) {
    var checked by rememberSaveable(model) { mutableStateOf(model.isChecked) }
    Switch(
        checked = model.isChecked,
        enabled = model.isCheckable,
        onCheckedChange = {
            checked = it
            model.onCheckedChange(it)
        }
    )
}
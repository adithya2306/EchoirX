package app.echoirx.presentation.screens.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.echoirx.R
import app.echoirx.domain.model.SearchHistory

@Composable
fun SearchHistoryItem(
    item: SearchHistory,
    onClick: (SearchHistory) -> Unit,
    onDelete: (SearchHistory) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = stringResource(R.string.cd_search_history_item),
            modifier = Modifier.size(24.dp)
                .clickable { onClick(item) }
        )
        Text(
            text = item.query,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
                .clickable { onClick(item) }
        )
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = stringResource(R.string.cd_search_history_delete),
            modifier = Modifier.size(24.dp)
                .clickable { onDelete(item) }
        )
    }
}
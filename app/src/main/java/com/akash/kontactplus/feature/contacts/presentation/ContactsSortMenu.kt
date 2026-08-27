package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder

@Composable
fun ContactsSortMenu(
    selectedSortOrder: ContactSortOrder,
    onSortOrderChanged: (ContactSortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = stringResource(R.string.contacts_sort)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOptionItem(
                label = stringResource(R.string.contacts_sort_ascending),
                selected = selectedSortOrder == ContactSortOrder.NameAscending,
                onClick = {
                    onSortOrderChanged(ContactSortOrder.NameAscending)
                    expanded = false
                }
            )
            SortOptionItem(
                label = stringResource(R.string.contacts_sort_descending),
                selected = selectedSortOrder == ContactSortOrder.NameDescending,
                onClick = {
                    onSortOrderChanged(ContactSortOrder.NameDescending)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun SortOptionItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text = label) },
        onClick = onClick,
        trailingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ContactsSortMenuPreview() {
    KontactPlusTheme {
        ContactsSortMenu(
            selectedSortOrder = ContactSortOrder.NameAscending,
            onSortOrderChanged = {}
        )
    }
}

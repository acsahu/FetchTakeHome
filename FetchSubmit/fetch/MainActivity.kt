package com.arnav.fetch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arnav.fetch.ui.theme.FetchTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.layout.statusBarsPadding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Remove this if necessary
        setContent {
            FetchTheme {
                FetchDataApp()
            }
        }
    }
}

@Composable
fun FetchDataApp(mainViewModel: MainViewModel = viewModel()) {
    val items by mainViewModel.items

    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            if (items.isNotEmpty()) {
                ItemList(items)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ItemList(items: List<Item>) {
    val expandedGroups = remember { mutableStateMapOf<Int, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        val groupedItems = items.groupBy { it.listId }
        groupedItems.forEach { (listId, itemsInList) ->
            item {
                val isExpanded = expandedGroups[listId] ?: false
                ListIdHeader(
                    listId = listId,
                    isExpanded = isExpanded,
                    onHeaderClick = {
                        expandedGroups[listId] = !isExpanded
                    }
                )
            }
            if (expandedGroups[listId] == true) {
                items(itemsInList) { item ->
                    ItemRow(item)
                }
            }
        }
    }
}

@Composable
fun ListIdHeader(
    listId: Int,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = ""
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Text(
                text = "List ID: $listId",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}

@Composable
fun ItemRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = item.name ?: "Unnamed Item",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

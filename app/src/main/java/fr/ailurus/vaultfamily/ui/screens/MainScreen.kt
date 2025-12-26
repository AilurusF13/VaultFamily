package fr.ailurus.vaultfamily.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.ui.components.EditEntryDialog
import fr.ailurus.vaultfamily.ui.components.EntryDisplay
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel

@Composable
fun MainScreen(viewModel: VaultViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editEntry by remember { mutableStateOf(Entry())}

    val uiState by viewModel.uiState.collectAsState()
    val entries = uiState.entries

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                    editEntry = Entry() // Adjonction donc on a un objet vide
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            EntriesDisplay(
                entries = entries,
                onItemClick = {}, // TODO(action simple de click)
                onItemLongClick = { entry ->
                    editEntry = entry
                    showAddDialog = true
                }
            )

            if (showAddDialog){
                EditEntryDialog(
                    onDismissRequest = {
                        showAddDialog = false
                    },
                    onConfirmation = { newEntry ->
                        viewModel.saveEntry(newEntry)
                        showAddDialog = false
                    },
                    onDelete = { oldEntry ->
                        viewModel.deleteEntry(oldEntry)
                        showAddDialog = false
                    },
                    editEntry = editEntry
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntriesDisplay(
    entries: List<Entry>,
    onItemClick: (Entry) -> Unit = {},
    onItemLongClick: (Entry) -> Unit = {}
) {
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(
            items = entries,
            key = { it.id }
        ) { entry ->
            Box(
                modifier  = Modifier.combinedClickable(
                    onClick = { },
                    onLongClick = { onItemLongClick(entry) }
                )
            ){
                EntryDisplay(entry)
            }
        }
    }
}
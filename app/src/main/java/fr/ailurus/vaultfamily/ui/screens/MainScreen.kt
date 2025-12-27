package fr.ailurus.vaultfamily.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
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
    val groups = uiState.groups

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
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                focusRequester.requestFocus() // Donne le focus au champ
                keyboardController?.show()    // Force l'ouverture du clavier
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = searchQuery,
                onValueChange = { newQuery ->
                    searchQuery = newQuery
                    viewModel.onSearchQueryChange(searchQuery)
                },
                singleLine = true,
                leadingIcon =  {
                    Icon( Icons.Default.Search, "Rechercher")
                },
                placeholder = {
                    Text("Rechercher")
                },
            )

            // group filter => filter chip
            // ! il faut avoir une liste de group dans le vault afin que ce soit foncitonnel
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(groups) { g ->
                    FilterChip(
                        onClick = {
                            if (uiState.groupQuery == g) {
                                viewModel.onGroupQueryChange("")
                            } else {
                                viewModel.onGroupQueryChange(g)
                            }
                        },
                        selected = uiState.groupQuery == g,
                        label = { Text(text = g) },
                        leadingIcon = if (uiState.groupQuery == g) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
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
                    editEntry = editEntry,
                    groups = groups
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
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
            Text("Aucun résultat")
        }
    } else {
        LazyColumn (
            modifier = Modifier.fillMaxWidth()
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
}
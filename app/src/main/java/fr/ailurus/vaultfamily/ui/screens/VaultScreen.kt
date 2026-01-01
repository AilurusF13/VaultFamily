@file:Suppress("AssignedValueIsNeverRead")


package fr.ailurus.vaultfamily.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.data.model.*
import fr.ailurus.vaultfamily.ui.components.EditEntryDialog
import fr.ailurus.vaultfamily.ui.components.EntryDisplay
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel

@Composable
fun VaultScreen(viewModel: VaultViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editEntry by remember { mutableStateOf(Entry())}

    val uiState by viewModel.uiState.collectAsState()
    val entries = uiState.entries
    val groups = uiState.groups

    val context = androidx.compose.ui.platform.LocalContext.current

    // Dans VaultScreen
    val ips by viewModel.ips.collectAsState()
    val deviceCount = ips.size

    val lastMessage by viewModel.lastMessage.collectAsState()
    // Dès que 'lastMessage' change de valeur, le bloc à l'intérieur est exécuté
    LaunchedEffect(lastMessage) {
        if (lastMessage != "Aucun message") { // On évite le toast au premier lancement
            android.widget.Toast.makeText(context, lastMessage, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

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
        },
        bottomBar = {
            Surface {
                Box(
                    Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center

                ){
                    Text(lastMessage)
                }
            }
        },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                val focusRequester = remember { FocusRequester() }
                val ips by viewModel.ips.collectAsState()

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    value = uiState.searchQuery, // Utilise le state du ViewModel
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, "Rechercher") },
                    placeholder = { Text("Rechercher dans le coffre") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val data = "Hello from ${android.os.Build.MODEL}".toByteArray()
                            viewModel.trySync(data)
                        }) {
                            // Changement de couleur si des gens sont connectés
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = if (ips.isNotEmpty()) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                    }
                )

                // group filter => filter chip
                // ! il faut avoir une liste de group dans le vault afin que ce soit foncitonnel
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(groups) { g ->
                        FilterChip(
                            onClick = {
                                if (uiState.groupQuery == g.groupId) {
                                    viewModel.onGroupQueryChange(0)
                                } else {
                                    viewModel.onGroupQueryChange(g.groupId)
                                }
                            },
                            selected = uiState.groupQuery == g.groupId,
                            label = {
                                Text(text = g.groupName)
                            },
                            leadingIcon = if (uiState.groupQuery == g.groupId) {
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
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {

            EntriesDisplay(
                entries = entries,
                groups = groups,
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
                    onConfirmation = { newEntry, password ->
                        viewModel.saveEntry(newEntry, password)
                        showAddDialog = false
                    },
                    onDelete = { oldEntry ->
                        viewModel.deleteEntry(oldEntry)
                        showAddDialog = false
                    },
                    editEntry = editEntry,
                    groups = groups // de meme on recupere le group avec l id
                )
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntriesDisplay(
    entries: List<Entry>,
    groups: List<Group>,
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
                key = { it.entryId }
            ) { entry ->
                Box(
                    modifier  = Modifier.combinedClickable(
                        onClick = { },
                        onLongClick = { onItemLongClick(entry) }
                    )
                ){
                    EntryDisplay(entry, groups)
                }
            }
        }
    }
}
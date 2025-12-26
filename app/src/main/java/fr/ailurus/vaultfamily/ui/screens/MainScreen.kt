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
import androidx.compose.ui.tooling.preview.Preview
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.repository.FakeVaultRepository
import fr.ailurus.vaultfamily.ui.components.EditEntryDialog
import fr.ailurus.vaultfamily.ui.components.EntryDisplay
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme

@Composable
fun MainScreen() {
        var showAddDialog by remember { mutableStateOf(false) }
        var editEntry by remember { mutableStateOf(Entry())}

        Scaffold (
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
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
                    fakeVault,
                    onItemClick = { entry ->
                        editEntry = entry
                        showAddDialog = true
                    }
                )

                if (showAddDialog){
                    EditEntryDialog(
                        onDismissRequest = {
                            showAddDialog = false
                            editEntry = Entry()
                        },
                        onConfirmation = { newEntry ->
                            fakeVault.saveEntry(newEntry)
                            showAddDialog = false
                            editEntry = Entry()
                        },
                        onDelete = { oldEntry ->
                            fakeVault.deleteEntry(oldEntry)
                            showAddDialog = false
                            editEntry = Entry()
                        },
                        editEntry = editEntry
                    )
                }
            }
        }
        addMockEntries()
}


fun addMockEntries(){
    fakeVault.saveEntry(
        Entry(
            siteWeb = "google.com",
            identifiant = "ailurus@gmail.com",
            password = "toto",
            group = "self"
        )
    )
    fakeVault.saveEntry(
        Entry(
            siteWeb = "amazon.fr",
            identifiant = "redhood@yahoo.fr",
            password = "tete",
            group = "family"
        )
    )
}


val fakeVault = FakeVaultRepository()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntriesDisplay(
    vault: FakeVaultRepository,
    onItemClick: (Entry) -> Unit = {}
) {

    val entriesState by vault.getAllEntries().collectAsState(initial = emptyList())
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(
            items = entriesState,
            key = { it.id }
        ) { entry ->
            Box(
                modifier  = Modifier.combinedClickable(
                    onClick = { },
                    onLongClick = { onItemClick(entry) }
                )
            ){
                EntryDisplay(entry)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntriesDisplayPreview() {
    VaultFamilyTheme {
        EntriesDisplay(fakeVault)
    }
}
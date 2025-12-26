
package fr.ailurus.vaultfamily
import androidx.compose.foundation.ExperimentalFoundationApi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaultFamilyTheme {

                var showAddDialog by remember { mutableStateOf(false) }
                var editEntry by remember { mutableStateOf(ContentLine())}

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
                        ContentList(
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
                                    editEntry = ContentLine()
                                },
                                onConfirmation = { newEntry ->
                                    fakeVault.saveEntry(newEntry)
                                    showAddDialog = false
                                    editEntry = ContentLine()
                                },
                                onDelete = { oldEntry ->
                                    fakeVault.deleteEntry(oldEntry)
                                    showAddDialog = false
                                    editEntry = ContentLine()
                                },
                                editEntry = editEntry
                            )
                        }
                    }
                }

                addMockEntries()
            }
        }
    }
}

fun addMockEntries(){
    fakeVault.saveEntry(ContentLine(
        siteWeb = "google.com",
        identifiant = "ailurus@gmail.com",
        password = "toto",
        group = "self"
    ))
    fakeVault.saveEntry(ContentLine(
        siteWeb = "amazon.fr",
        identifiant = "redhood@yahoo.fr",
        password = "tete",
        group = "family"
    ))
}

val fakeVault = FakeVaultRepository()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentList(
    vault: FakeVaultRepository,
    onItemClick: (ContentLine) -> Unit = {}
) {

    val entriesState by vault.getAllEntries().collectAsState(initial = emptyList())
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(
            items = entriesState,
            key = { it.id }
        ) { line ->
            Box(
                modifier  = Modifier.combinedClickable(
                    onClick = { },
                    onLongClick = { onItemClick(line) }
                )
            ){
               ContentLineDisplay(line)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentListPreview() {
    VaultFamilyTheme {
        ContentList(fakeVault)
    }
}
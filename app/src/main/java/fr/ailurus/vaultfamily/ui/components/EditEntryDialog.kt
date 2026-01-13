package fr.ailurus.vaultfamily.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.model.EntrySecret
import fr.ailurus.vaultfamily.data.model.Group
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Entry, EntrySecret) -> Unit,
    onDelete: (Entry) -> Unit,
    editEntry: Entry,
    groups: List<Group>
) {
    var siteWeb by remember { mutableStateOf(editEntry.entrySite) }
    var identifiant by remember { mutableStateOf(editEntry.entryUser) }
    var password by remember { mutableStateOf("") } // on entre le nouau mot de passe

    var groupId by remember { mutableStateOf(editEntry.groupId )}
    if (groupId == 0L) {groupId =1L }

    var groupExtended by remember { mutableStateOf(false) }

    val enableDeleteButton = (editEntry.entryId != 0L)
    var enableDeleteDialog by remember { mutableStateOf(false) }

    fun Long?.toGroup() = groups.find { it.groupId == this } ?: Group(
        -1, "Not Found"
    )

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(text = "Ajouter mot de passe")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = siteWeb,
                    onValueChange = {siteWeb = it},
                    label = { Text("Site Web") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = identifiant,
                    onValueChange = {identifiant = it},
                    label = { Text("Identifiant") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    label = { Text("Mot de passe") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Drop down menu for group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box{
                        Button(
                            onClick = {
                                groupExtended = !groupExtended
                            }
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "groupe"
                            )
                            Text(text = groupId.toGroup().groupName)
                        }
                        DropdownMenu(
                            expanded = groupExtended,
                            onDismissRequest = {groupExtended = false}
                        ) {
                            for (g in groups){
                                DropdownMenuItem(
                                    text = { Text(text = g.groupName)},
                                    onClick = {
                                        groupId = g.groupId
                                        groupExtended = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ){
                    if (enableDeleteButton){
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            modifier = Modifier
                                .clickable { enableDeleteDialog = true }
                        )
                    }
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val updatedEntry = editEntry.copy(
                            entrySite = siteWeb,
                            entryUser = identifiant,
                            groupId = groupId
                        )
                        val secret = EntrySecret(
                            encryptedPassword = password.toByteArray()
                        )

                        // ACTION MANQUANTE :
                        onConfirmation(updatedEntry, secret) // Ou viewModel.save(updatedEntry, secret)
                        enableDeleteDialog = false
                    },
                    enabled = siteWeb.isNotBlank() && identifiant.isNotBlank()
                ) {
                    Text(text = "Valider")
                }
            }
        },
    )
    // On veut un boutton icon de supression
    if (enableDeleteDialog) {
        AlertDialog(
            onDismissRequest = { enableDeleteDialog = false },
            title = { Text("Voulez-vous supprimer l'élément ?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(editEntry)
                    enableDeleteDialog = false
                }) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                Button(onClick = { enableDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

//@Preview
//@Composable
//fun EditEntryDialogPreview() {
//    VaultFamilyTheme {
//        EditEntryDialog(onDismissRequest =  {}, onConfirmation = {}, onDelete = {}, editEntry = Entry(
//            100, "google.com", "franck", "famille", "pswd"
//        ), groups = listOf("famille", "travail")
//        )
//    }
//}
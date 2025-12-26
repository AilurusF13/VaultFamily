package fr.ailurus.vaultfamily.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import fr.ailurus.vaultfamily.data.model.Entry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Entry) -> Unit,
    onDelete: (Entry) -> Unit,
    editEntry: Entry,
) {
    var siteWeb by remember { mutableStateOf(editEntry.siteWeb) }
    var identifiant by remember { mutableStateOf(editEntry.identifiant) }
    var password by remember { mutableStateOf(editEntry.password) }

    var group by remember { mutableStateOf(editEntry.group )}

    var groupExtended by remember { mutableStateOf(false) }

    val defaultGroup = "self"

    val enableDeleteButton = (editEntry.id != 0L)
    var enableDeleteDialog by remember { mutableStateOf(false) }
    
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
                            Text(text = group)
                        }
                        DropdownMenu(
                            expanded = groupExtended,
                            onDismissRequest = {groupExtended = false}
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = defaultGroup)},
                                onClick = {
                                    group = defaultGroup
                                    groupExtended = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = "famille")
                                },
                                onClick = {
                                    group = "famille"
                                    groupExtended = false
                                }
                            )
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
                        val newEntry = editEntry.copy(
                            siteWeb = siteWeb,
                            identifiant = identifiant,
                            group = group,
                            password = password
                        )
                        onConfirmation(newEntry)
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

@Preview
@Composable
fun EditEntryDialogPreview() {
    VaultFamilyTheme {
        EditEntryDialog(onDismissRequest =  {}, onConfirmation = {}, onDelete = {}, editEntry = Entry(
            100, "google.com", "franck", "famille", "pswd"
        )
        )
    }
}
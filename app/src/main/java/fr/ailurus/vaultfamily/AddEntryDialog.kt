package fr.ailurus.vaultfamily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (ContentLine) -> Unit
) {
    var siteWeb by remember { mutableStateOf("") }
    var identifiant by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val defaultGroup = "self"
    var group by remember { mutableStateOf(defaultGroup)}

    var groupExtended by remember { mutableStateOf(false) }

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
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            groupExtended = !groupExtended
                        }
                    ) {
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
                            text = { Text(text = "famille") },
                            onClick = {
                                group = "famille"
                                groupExtended = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newEntry = ContentLine(
                        siteWeb = siteWeb,
                        identifiant = identifiant,
                        group = group,
                        password = password
                    )
                    onConfirmation(newEntry)
                },
                enabled = siteWeb.isNotBlank() && identifiant.isNotBlank()
            ) {
                Text(text = "Ajouter")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = "Annuler")
            }
        }
    )
}

@Preview
@Composable
fun AddEntryDialogPreview() {
    VaultFamilyTheme {
        AddEntryDialog({}, {})
    }
}
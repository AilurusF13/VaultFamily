package fr.ailurus.vaultfamily.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextAlign
import fr.ailurus.vaultfamily.data.model.Entry

@Composable
fun EntryDisplay(entry: Entry) {
    Card (
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.entrySite[0].uppercase(),
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = entry.entrySite,
                    textAlign = TextAlign.Left
                )

                Text(
                    text = entry.entryUser,
                    textAlign = TextAlign.Left
                )
            }
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "none temporary", // TODO recuperer le label
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryDisplayPreview(){
    VaultFamilyTheme {
        EntryDisplay(
            Entry(
                entrySite = "google.com/login",
                entryUser = "example@gmail.com",
                groupId = 0
            )
        )
    }
}
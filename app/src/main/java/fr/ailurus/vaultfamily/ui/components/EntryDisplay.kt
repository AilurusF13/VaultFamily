package fr.ailurus.vaultfamily.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.model.Group

@Composable
fun EntryDisplay(entry: Entry, groups: List<Group>) {
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
                    text = groups.find { it.groupId == entry.groupId }?.groupName ?: "Error",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
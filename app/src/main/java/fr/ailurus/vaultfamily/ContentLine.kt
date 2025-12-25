package fr.ailurus.vaultfamily
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

data class ContentLine(
    val id : Long = 0,
    val siteWeb : String,
    val identifiant : String,
    val group : String
)

@Composable
fun ContentLineDisplay(cl: ContentLine) {
    Card (
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cl.siteWeb[0].uppercase(),
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = cl.siteWeb,
                    textAlign = TextAlign.Left
                )

                Text(
                    text = cl.identifiant,
                    textAlign = TextAlign.Left
                )
            }
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cl.group,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentLineDisplayPreview(){
    VaultFamilyTheme {
        ContentLineDisplay(
            ContentLine(
                siteWeb = "google.com/login",
                identifiant = "example@gmail.com",
                group = "self"
            )
        )
    }
}
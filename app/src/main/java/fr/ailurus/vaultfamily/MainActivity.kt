@file:Suppress("SpellCheckingInspection")

package fr.ailurus.vaultfamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                        ContentList(fakeVault)

                        if (showAddDialog){
                            AddEntryDialog(
                                onDismissRequest = { showAddDialog = false },
                                onConfirmation = { newEntry ->
                                    fakeVault.saveEntry(newEntry)
                                    showAddDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

val fakeVault = FakeVaultRepository()
val mockContentLines = listOf(
    ContentLine(0, "github.com", "user_alpha_01", "Developers"),
    ContentLine(0, "stackoverflow.com", "dev_expert_99", "Community"),
    ContentLine(0, "aws.amazon.com", "admin_root_prod", "Infrastructure"),
    ContentLine(0, "gitlab.com", "ci_runner_04", "DevOps"),
    ContentLine(0, "coursera.org", "student_math_l3", "Education"),
    ContentLine(0, "linkedin.com", "recruiter_it_02", "HR"),
    ContentLine(0, "bitbucket.org", "repo_manager_x", "Developers"),
    ContentLine(0, "medium.com", "writer_tech_blog", "Content"),
    ContentLine(0, "docker.com", "registry_puller", "Infrastructure"),
    ContentLine(0, "reddit.com", "math_moderator", "Community"),
    ContentLine(0, "netflix.com", "famille_durand", "Streaming"),
    ContentLine(0, "spotify.com", "music_lover_92", "Entertainment"),
    ContentLine(0, "disneyplus.com", "kids_account", "Streaming"),
    ContentLine(0, "amazon.fr", "prime_member_01", "Shopping"),
    ContentLine(0, "leboncoin.fr", "vendeur_du_31", "Shopping"),
    ContentLine(0, "revolut.com", "fintech_user", "Finance"),
    ContentLine(0, "binance.com", "crypto_trader_x", "Finance"),
    ContentLine(0, "paypal.com", "ecom_buyer", "Finance"),
    ContentLine(0, "discord.com", "gamer_tag_pro", "Social"),
    ContentLine(0, "twitter.com", "news_watcher", "Social"),
    ContentLine(0, "instagram.com", "photo_fanatic", "Social"),
    ContentLine(0, "steampowered.com", "valve_fan_88", "Gaming"),
    ContentLine(0, "epicgames.com", "fortnite_player", "Gaming"),
    ContentLine(0, "nintendo.com", "switch_user_jp", "Gaming"),
    ContentLine(0, "apple.com", "icloud_storage", "Cloud"),
    ContentLine(0, "dropbox.com", "file_sync_pro", "Cloud"),
    ContentLine(0, "notion.so", "productivity_guru", "Work"),
    ContentLine(0, "slack.com", "workspace_admin", "Work"),
    ContentLine(0, "zoom.us", "meeting_host_01", "Work"),
    ContentLine(0, "protonmail.com", "privacy_first", "Security")
)

var mockContentIndex = 0
fun addMockContent(){
    val entry = mockContentLines[mockContentIndex % mockContentLines.size]
    fakeVault.saveEntry(entry)
    mockContentIndex += 1
}

@Composable
fun ContentList(vault: FakeVaultRepository) {

    val entriesState by vault.getAllEntries().collectAsState(initial = emptyList())
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(
            items = entriesState,
            key = { it.id }
        ) { line ->
            ContentLineDisplay(line)
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
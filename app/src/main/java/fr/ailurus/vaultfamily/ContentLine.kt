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
    val siteWeb : String,
    val identifiant : String,
    val group : String
)

val mockContentLines = listOf(
    ContentLine("github.com", "user_alpha_01", "Developers"),
    ContentLine("stackoverflow.com", "dev_expert_99", "Community"),
    ContentLine("aws.amazon.com", "admin_root_prod", "Infrastructure"),
    ContentLine("gitlab.com", "ci_runner_04", "DevOps"),
    ContentLine("coursera.org", "student_math_l3", "Education"),
    ContentLine("linkedin.com", "recruiter_it_02", "HR"),
    ContentLine("bitbucket.org", "repo_manager_x", "Developers"),
    ContentLine("medium.com", "writer_tech_blog", "Content"),
    ContentLine("docker.com", "registry_puller", "Infrastructure"),
    ContentLine("reddit.com", "math_moderator", "Community"),
    ContentLine("netflix.com", "famille_durand", "Streaming"),
    ContentLine("spotify.com", "music_lover_92", "Entertainment"),
    ContentLine("disneyplus.com", "kids_account", "Streaming"),
    ContentLine("amazon.fr", "prime_member_01", "Shopping"),
    ContentLine("leboncoin.fr", "vendeur_du_31", "Shopping"),
    ContentLine("revolut.com", "fintech_user", "Finance"),
    ContentLine("binance.com", "crypto_trader_x", "Finance"),
    ContentLine("paypal.com", "ecom_buyer", "Finance"),
    ContentLine("discord.com", "gamer_tag_pro", "Social"),
    ContentLine("twitter.com", "news_watcher", "Social"),
    ContentLine("instagram.com", "photo_fanatic", "Social"),
    ContentLine("steampowered.com", "valve_fan_88", "Gaming"),
    ContentLine("epicgames.com", "fortnite_player", "Gaming"),
    ContentLine("nintendo.com", "switch_user_jp", "Gaming"),
    ContentLine("apple.com", "icloud_storage", "Cloud"),
    ContentLine("dropbox.com", "file_sync_pro", "Cloud"),
    ContentLine("notion.so", "productivity_guru", "Work"),
    ContentLine("slack.com", "workspace_admin", "Work"),
    ContentLine("zoom.us", "meeting_host_01", "Work"),
    ContentLine("protonmail.com", "privacy_first", "Security")
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
                "google.com/login",
                "example@gmail.com",
                "self"
            )
        )
    }
}
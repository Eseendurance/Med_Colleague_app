package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MedicalReferenceSite(
    val name: String,
    val category: String,
    val description: String,
    val url: String
)

val ReferenceSitesList = listOf(
    MedicalReferenceSite(
        name = "NCBI StatPearls",
        category = "Board Concepts & Guidelines",
        description = "Peer-reviewed medical articles & clinical decision summaries for board prep.",
        url = "https://www.ncbi.nlm.nih.gov/books/NBK430685/"
    ),
    MedicalReferenceSite(
        name = "PubMed Central",
        category = "Primary Literature",
        description = "National Library of Medicine database for biomedical clinical trials.",
        url = "https://pubmed.ncbi.nlm.nih.gov/"
    ),
    MedicalReferenceSite(
        name = "Medscape Reference",
        category = "Drug MoA & Guidelines",
        description = "Clinical drug interactions, dosages, disease manifestations & procedures.",
        url = "https://reference.medscape.com/"
    ),
    MedicalReferenceSite(
        name = "CDC Clinical Guidelines",
        category = "Infectious Disease & Triage",
        description = "Center for Disease Control treatment protocols & emergency alerts.",
        url = "https://www.cdc.gov/"
    ),
    MedicalReferenceSite(
        name = "WHO Disease Outbreaks",
        category = "Global Health Protocols",
        description = "World Health Organization international treatment & diagnostic guidance.",
        url = "https://www.who.int/emergencies/diseases"
    ),
    MedicalReferenceSite(
        name = "AHA Circulation Guidelines",
        category = "Cardiology & ACLS",
        description = "American Heart Association emergency cardiac care & stroke protocols.",
        url = "https://cchp.heart.org/"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceDrawerBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.testTag("reference_drawer_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "📚 Reputable Medical Reference Directory",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tap any reference to open official clinical literature & verification portals.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search references or guidelines...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reference_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            val filtered = ReferenceSitesList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                items(filtered) { site ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(site.url))
                                context.startActivity(intent)
                            }
                            .testTag("reference_site_card_${site.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = site.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = site.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = site.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open Link",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

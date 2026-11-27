package com.example.tpahmaddaoudjetpackcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpahmaddaoudjetpackcompose.ui.theme.TPAhmadDAOUDJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPAhmadDAOUDJetpackComposeTheme {
                ProfileScreen(
                    name = "Ahmad Daoud",
                    role = "Développeur Android junior",
                    description = "Etudiant en M1 à l'ESGI, je développe des applis Android avec Kotlin et Jetpack Compose.",
                    skills = sampleSkills(),
                    projects = sampleProjects(),
                    isAvailable = true,
                    availability = "Disponible pour une alternance",
                    email = "ahmad.daoud@exemple.fr",
                    location = "Paris",
                    onContactClick = {
                        // affichera éventuellement le contact
                    }
                )
            }
        }
    }
}

// Un projet du portfolio
data class Project(
    val id: Int,
    val title: String,
    val description: String,
    val technologies: List<String>
)

// Données en dur pour l'appli, pas d'API
fun sampleProjects(): List<Project> = listOf(
    Project(
        id = 1,
        title = "Product Explorer",
        description = "Appli de catalogue produits faite pendant les TP Compose.",
        technologies = listOf("Kotlin", "Jetpack Compose", "ViewModel")
    ),
    Project(
        id = 2,
        title = "Fiche Profil",
        description = "Premier écran Compose avec une fiche de présentation.",
        technologies = listOf("Kotlin", "Material 3")
    ),
    Project(
        id = 3,
        title = "Site perso",
        description = "Petit site pour présenter mes projets et mon CV.",
        technologies = listOf("HTML", "CSS", "Git")
    ),
    Project(
        id = 4,
        title = "Profil développeur interactif",
        description = "Cette appli : mon profil avec recherche et favoris sur les projets.",
        technologies = listOf("Kotlin", "Jetpack Compose", "StateFlow")
    ),
    Project(
        id = 5,
        title = "Blockchain Python",
        description = "Petite blockchain pour comprendre les blocs, le hachage et la validation.",
        technologies = listOf("Python", "Git")
    ),
    Project(
        id = 6,
        title = "Algorithmique Python",
        description = "Exercices d'algo : tris, recherche et récursivité.",
        technologies = listOf("Python")
    )
)

// Etat de l'écran principal, publié par le ViewModel
data class ProfileUiState(
    val projects: List<Project> = emptyList(),
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val favoriteProjectIds: Set<Int> = emptySet()
)

// Les compétences affichées dans la LazyRow
fun sampleSkills(): List<String> = listOf(
    "Kotlin",
    "Jetpack Compose",
    "Material 3",
    "Git",
    "Android Studio"
)

// Ecran principal de l'appli, tout l'écran est une LazyColumn
@Composable
fun ProfileScreen(
    name: String,
    role: String,
    description: String,
    skills: List<String>,
    projects: List<Project>,
    isAvailable: Boolean,
    availability: String,
    email: String,
    location: String,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProfileHeader(
                    name = name,
                    role = role,
                    description = description,
                    isAvailable = isAvailable
                )
            }
            item {
                Text(
                    text = "Compétences",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                // liste horizontale, un SkillChip par compétence
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(skills) { skill ->
                        SkillChip(skill = skill)
                    }
                }
            }
            item {
                Text(
                    text = "Projets",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // une ProjectCard par projet, l'id sert de clé
            items(projects, key = { it.id }) { project ->
                ProjectCard(project = project)
            }
            item {
                ContactCard(
                    email = email,
                    location = location,
                    availability = availability
                )
                ContactButton(
                    text = "Contacter",
                    onClick = onContactClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

// Carte d'un projet
@Composable
fun ProjectCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Projet",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.technologies.joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Carte contact avec les infos en lecture seule
@Composable
fun ContactCard(
    email: String,
    location: String,
    availability: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Contact",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            ContactInfo(icon = Icons.Default.Email, label = "Email", value = email)
            ContactInfo(icon = Icons.Default.LocationOn, label = "Localisation", value = location)
            ContactInfo(icon = Icons.Default.Event, label = "Disponibilité", value = availability)
        }
    }
}

// Une ligne de contact : icone + champ en lecture seule
@Composable
fun ContactInfo(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        // readOnly : on affiche juste l'info, on ne peut pas la modifier
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            modifier = Modifier.weight(1f)
        )
    }
}

// Bouton contacter, le clic est géré par le parent
@Composable
fun ContactButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

// En-tete : avatar, nom, role et description
@Composable
fun ProfileHeader(
    name: String,
    role: String,
    description: String,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarWithBadge(isAvailable = isAvailable)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = role,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

// Avatar rond avec le badge "Dispo" par dessus
@Composable
fun AvatarWithBadge(isAvailable: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = "Photo de profil",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
        // le badge s'affiche seulement si le dev est dispo
        if (isAvailable) {
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiary
            ) {
                Text(
                    text = "Dispo",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// Une compétence affichée comme une petite pastille
@Composable
fun SkillChip(skill: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = skill,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// Preview 1 : profil android junior dispo, en clair et en sombre
// heightDp pour voir tout l'écran d'un coup
@Preview(showBackground = true, heightDp = 1300, name = "Junior Android - clair")
@Preview(
    showBackground = true,
    heightDp = 1300,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Junior Android - sombre"
)
@Composable
fun ProfileJuniorPreview() {
    TPAhmadDAOUDJetpackComposeTheme {
        ProfileScreen(
            name = "Ahmad Daoud",
            role = "Développeur Android junior",
            description = "Etudiant en M1 à l'ESGI, je développe des applis Android avec Kotlin et Jetpack Compose.",
            skills = listOf("Kotlin", "Jetpack Compose", "Git"),
            projects = sampleProjects(),
            isAvailable = true,
            availability = "Disponible pour une alternance",
            email = "ahmad.daoud@exemple.fr",
            location = "Paris",
            onContactClick = {}
        )
    }
}

// Preview 2 : profil full-stack pas dispo, avec d'autres compétences
@Preview(showBackground = true, name = "Full-stack pas dispo")
@Composable
fun ProfileFullStackPreview() {
    TPAhmadDAOUDJetpackComposeTheme {
        ProfileScreen(
            name = "Sarah Martin",
            role = "Développeuse full-stack",
            description = "5 ans d'expérience en web et mobile, surtout sur des API et du React.",
            skills = listOf("TypeScript", "React", "Node.js", "PostgreSQL", "Docker"),
            projects = listOf(
                Project(1, "Appli de réservation", "Site de réservation pour un restaurant.", listOf("React", "Node.js")),
                Project(2, "API de paiement", "API pour gérer les paiements d'une boutique.", listOf("TypeScript", "PostgreSQL"))
            ),
            isAvailable = false,
            availability = "Pas disponible, en mission jusqu'en mars 2027",
            email = "sarah.martin@exemple.fr",
            location = "Lyon",
            onContactClick = {}
        )
    }
}

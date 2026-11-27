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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpahmaddaoudjetpackcompose.ui.theme.TPAhmadDAOUDJetpackComposeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPAhmadDAOUDJetpackComposeTheme {
                ProfileContainer()
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

// Garde les projets qui correspondent à la recherche (titre, description ou techno)
// et, si le filtre est actif, seulement les favoris
fun filterProjects(
    projects: List<Project>,
    searchQuery: String,
    showOnlyFavorites: Boolean,
    favoriteProjectIds: Set<Int>
): List<Project> {
    val query = searchQuery.trim()
    return projects.filter { project ->
        val matchesSearch = query.isEmpty() ||
            project.title.contains(query, ignoreCase = true) ||
            project.description.contains(query, ignoreCase = true) ||
            project.technologies.any { it.contains(query, ignoreCase = true) }
        val matchesFavorite = !showOnlyFavorites || project.id in favoriteProjectIds
        matchesSearch && matchesFavorite
    }
}

// ViewModel de l'écran profil : il garde l'état et réagit aux actions de l'utilisateur
class ProfileViewModel : ViewModel() {

    // tous les projets, la liste affichée est calculée à partir de celle-ci
    private val allProjects = sampleProjects()

    // état modifiable, privé : seul le ViewModel peut le changer
    private val _uiState = MutableStateFlow(ProfileUiState(projects = allProjects))

    // version publique en lecture seule, observée par l'interface
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        publish { it.copy(searchQuery = query) }
    }

    fun onFavoriteClick(projectId: Int) {
        publish { state ->
            val favorites = state.favoriteProjectIds
            state.copy(
                favoriteProjectIds = if (projectId in favorites) favorites - projectId else favorites + projectId
            )
        }
    }

    fun onToggleFavoriteFilter() {
        publish { it.copy(showOnlyFavorites = !it.showOnlyFavorites) }
    }

    // applique le changement puis recalcule la liste affichée avant de publier le nouvel état
    private fun publish(change: (ProfileUiState) -> ProfileUiState) {
        _uiState.update { state ->
            val newState = change(state)
            newState.copy(
                projects = filterProjects(
                    projects = allProjects,
                    searchQuery = newState.searchQuery,
                    showOnlyFavorites = newState.showOnlyFavorites,
                    favoriteProjectIds = newState.favoriteProjectIds
                )
            )
        }
    }
}

// Fait le lien entre le ViewModel et Compose : collecte l'état et le passe à l'écran
@Composable
fun ProfileContainer(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        name = "Ahmad Daoud",
        role = "Développeur Android junior",
        description = "Etudiant en M1 à l'ESGI, je développe des applis Android avec Kotlin et Jetpack Compose.",
        skills = sampleSkills(),
        projects = uiState.projects,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        showOnlyFavorites = uiState.showOnlyFavorites,
        onToggleFavoriteFilter = viewModel::onToggleFavoriteFilter,
        favoriteProjectIds = uiState.favoriteProjectIds,
        onFavoriteClick = viewModel::onFavoriteClick,
        isAvailable = true,
        availability = "Disponible pour une alternance",
        email = "ahmad.daoud@exemple.fr",
        location = "Paris",
        onContactClick = {
            // affichera éventuellement le contact
        },
        modifier = modifier
    )
}

// Les compétences affichées dans la LazyRow
fun sampleSkills(): List<String> = listOf(
    "Kotlin",
    "Jetpack Compose",
    "Material 3",
    "Git",
    "Android Studio"
)

// Ecran principal de l'appli, tout l'écran est une LazyColumn
// stateless : il reçoit l'état en paramètres et remonte les actions par des callbacks
@Composable
fun ProfileScreen(
    name: String,
    role: String,
    description: String,
    skills: List<String>,
    projects: List<Project>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showOnlyFavorites: Boolean,
    onToggleFavoriteFilter: () -> Unit,
    favoriteProjectIds: Set<Int>,
    onFavoriteClick: (Int) -> Unit,
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
            item {
                ProjectSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange
                )
            }
            item {
                ProjectFilterRow(
                    showOnlyFavorites = showOnlyFavorites,
                    onToggleFavoriteFilter = onToggleFavoriteFilter,
                    visibleCount = projects.size
                )
            }
            if (projects.isEmpty()) {
                item {
                    EmptyProjectsMessage(showOnlyFavorites = showOnlyFavorites)
                }
            }
            // une ProjectCard par projet, l'id sert de clé
            items(projects, key = { it.id }) { project ->
                ProjectCard(
                    project = project,
                    isFavorite = project.id in favoriteProjectIds,
                    onFavoriteClick = { onFavoriteClick(project.id) }
                )
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

// Champ de recherche des projets
@Composable
fun ProjectSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(text = "Rechercher un projet") },
        placeholder = { Text(text = "Titre, techno ou description") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    )
}

// Filtre "favoris uniquement" + nombre de projets affichés
@Composable
fun ProjectFilterRow(
    showOnlyFavorites: Boolean,
    onToggleFavoriteFilter: () -> Unit,
    visibleCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = showOnlyFavorites,
            onClick = onToggleFavoriteFilter,
            label = { Text(text = "Favoris uniquement") },
            leadingIcon = {
                Icon(
                    imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = projectCountLabel(visibleCount),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// "1 projet affiché", "4 projets affichés" (0 reste au singulier en français)
fun projectCountLabel(count: Int): String {
    val plural = if (count > 1) "s" else ""
    return "$count projet$plural affiché$plural"
}

// Message quand aucun projet n'est affiché
@Composable
fun EmptyProjectsMessage(showOnlyFavorites: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (showOnlyFavorites) {
            "Aucun projet favori ne correspond."
        } else {
            "Aucun projet ne correspond à la recherche."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
}

// Carte d'un projet, avec un coeur pour l'ajouter ou le retirer des favoris
@Composable
fun ProjectCard(
    project: Project,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Column(modifier = Modifier.weight(1f)) {
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
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Carte contact avec les infos en lecture seule
// l'état "infos affichées ou non" reste local : seule cette carte en a besoin,
// rememberSaveable le garde quand on tourne l'écran
@Composable
fun ContactCard(
    email: String,
    location: String,
    availability: String,
    modifier: Modifier = Modifier
) {
    var showContactInfo by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Contact",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { showContactInfo = !showContactInfo }) {
                Icon(
                    imageVector = if (showContactInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showContactInfo) {
                        "Masquer les informations de contact"
                    } else {
                        "Afficher les informations de contact"
                    }
                )
            }
            if (showContactInfo) {
                ContactInfo(icon = Icons.Default.Email, label = "Email", value = email)
                ContactInfo(icon = Icons.Default.LocationOn, label = "Localisation", value = location)
                ContactInfo(icon = Icons.Default.Event, label = "Disponibilité", value = availability)
            }
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

// Preview 1 : état de départ, recherche vide, tous les projets affichés, aucun favori
// en clair et en sombre, heightDp pour voir tout l'écran d'un coup
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
            searchQuery = "",
            onSearchQueryChange = {},
            showOnlyFavorites = false,
            onToggleFavoriteFilter = {},
            favoriteProjectIds = emptySet(),
            onFavoriteClick = {},
            isAvailable = true,
            availability = "Disponible pour une alternance",
            email = "ahmad.daoud@exemple.fr",
            location = "Paris",
            onContactClick = {}
        )
    }
}

// Preview 2 : recherche "kotlin" active, filtre favoris activé, 3 projets en favoris
// la liste est calculée avec filterProjects, comme dans le ViewModel, mais sans lui
@Preview(showBackground = true, heightDp = 1300, name = "Recherche + favoris")
@Composable
fun ProfileSearchFavoritesPreview() {
    val searchQuery = "kotlin"
    val favoriteProjectIds = setOf(1, 4, 5)
    TPAhmadDAOUDJetpackComposeTheme {
        ProfileScreen(
            name = "Ahmad Daoud",
            role = "Développeur Android junior",
            description = "Etudiant en M1 à l'ESGI, je développe des applis Android avec Kotlin et Jetpack Compose.",
            skills = listOf("Kotlin", "Jetpack Compose", "Git"),
            projects = filterProjects(
                projects = sampleProjects(),
                searchQuery = searchQuery,
                showOnlyFavorites = true,
                favoriteProjectIds = favoriteProjectIds
            ),
            searchQuery = searchQuery,
            onSearchQueryChange = {},
            showOnlyFavorites = true,
            onToggleFavoriteFilter = {},
            favoriteProjectIds = favoriteProjectIds,
            onFavoriteClick = {},
            isAvailable = true,
            availability = "Disponible pour une alternance",
            email = "ahmad.daoud@exemple.fr",
            location = "Paris",
            onContactClick = {}
        )
    }
}

// Preview 3 : profil full-stack pas dispo, avec d'autres compétences
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
            searchQuery = "",
            onSearchQueryChange = {},
            showOnlyFavorites = false,
            onToggleFavoriteFilter = {},
            favoriteProjectIds = emptySet(),
            onFavoriteClick = {},
            isAvailable = false,
            availability = "Pas disponible, en mission jusqu'en mars 2027",
            email = "sarah.martin@exemple.fr",
            location = "Lyon",
            onContactClick = {}
        )
    }
}

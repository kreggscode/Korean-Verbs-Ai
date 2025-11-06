package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kreggscode.koreanverbs.data.models.KoreanVerb
import com.kreggscode.koreanverbs.data.repository.VerbRepository
import com.kreggscode.koreanverbs.data.repository.FavoritesRepository
import com.kreggscode.koreanverbs.navigation.Screen
import com.kreggscode.koreanverbs.ui.components.*
import com.kreggscode.koreanverbs.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun VerbsScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { VerbRepository(context) }
    val favoritesRepository = remember { FavoritesRepository(context) }
    val scope = rememberCoroutineScope()
    
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var allVerbs by remember { mutableStateOf<List<KoreanVerb>>(emptyList()) }
    var displayedVerbs by remember { mutableStateOf<List<KoreanVerb>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(ViewMode.CATEGORIES) }
    val favoriteIds by favoritesRepository.favoritesFlow.collectAsState()
    
    // Load categories first (fast), then verbs in background
    LaunchedEffect(Unit) {
        scope.launch {
            // Load categories immediately - show them right away
            categories = repository.getCategories()
            isLoading = false
            
            // Load all verbs in background (for search/list views)
            allVerbs = repository.getAllVerbs()
            if (displayedVerbs.isEmpty()) {
                displayedVerbs = allVerbs.take(20)
            }
        }
    }
    
    LaunchedEffect(searchQuery, selectedCategory, viewMode, favoriteIds) {
        scope.launch {
            displayedVerbs = when {
                searchQuery.isNotEmpty() -> {
                    repository.searchVerbs(searchQuery)
                }
                viewMode == ViewMode.FAVORITES -> {
                    repository.getFavoriteVerbs(favoriteIds)
                }
                selectedCategory != null -> {
                    repository.getVerbsByCategory(selectedCategory!!)
                }
                else -> {
                    allVerbs.take(20)
                }
            }
        }
    }
    
    // Clean solid background - no gradients, no purple
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header with Search
            VerbsHeader(
                searchQuery = searchQuery,
                onSearchChange = { 
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewMode = ViewMode.LIST
                        selectedCategory = null
                    }
                },
                viewMode = viewMode,
                onViewModeChange = { 
                    viewMode = it
                    if (it != ViewMode.LIST) {
                        selectedCategory = null
                        searchQuery = ""
                    }
                },
                favoriteCount = favoriteIds.size
            )
            
            // Content
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                }
            ) { mode ->
                when (mode) {
                    ViewMode.CATEGORIES -> {
                        CategoriesGrid(
                            categories = categories,
                            onCategoryClick = { category ->
                                selectedCategory = category
                                viewMode = ViewMode.LIST
                                navController.navigate(Screen.VerbCategory.createRoute(category))
                            }
                        )
                    }
                    ViewMode.LIST -> {
                        VerbsList(
                            verbs = displayedVerbs,
                            onVerbClick = { verb ->
                                navController.navigate(Screen.VerbDetail.createRoute(verb.id))
                            },
                            selectedCategory = selectedCategory,
                            onClearCategory = {
                                selectedCategory = null
                                searchQuery = ""
                            }
                        )
                    }
                    ViewMode.FAVORITES -> {
                        VerbsList(
                            verbs = displayedVerbs,
                            onVerbClick = { verb ->
                                navController.navigate(Screen.VerbDetail.createRoute(verb.id))
                            },
                            selectedCategory = null,
                            onClearCategory = {}
                        )
                    }
                }
            }
        }
    }
}

enum class ViewMode {
    CATEGORIES, LIST, FAVORITES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerbsHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    favoriteCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Korean Verbs",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(16.dp)
                ),
            placeholder = { 
                Text(
                    "Search verbs...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // View Mode Toggle with ScrollableTabRow for better text handling
        ScrollableTabRow(
            selectedTabIndex = when (viewMode) {
                ViewMode.CATEGORIES -> 0
                ViewMode.LIST -> 1
                ViewMode.FAVORITES -> 2
            },
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            Tab(
                selected = viewMode == ViewMode.CATEGORIES,
                onClick = { onViewModeChange(ViewMode.CATEGORIES) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Category,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Categories",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
            Tab(
                selected = viewMode == ViewMode.LIST,
                onClick = { onViewModeChange(ViewMode.LIST) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "All Verbs",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
            Tab(
                selected = viewMode == ViewMode.FAVORITES,
                onClick = { onViewModeChange(ViewMode.FAVORITES) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (viewMode == ViewMode.FAVORITES) PremiumPink else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "Favorites",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (favoriteCount > 0) {
                            Badge(
                                containerColor = PremiumPink,
                                contentColor = Color.White
                            ) {
                                Text(
                                    favoriteCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CategoriesGrid(
    categories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            val gradientColors = when (index % 6) {
                0 -> listOf(PremiumPurple, PremiumPink)
                1 -> listOf(PremiumIndigo, PremiumTeal)
                2 -> listOf(PremiumTeal, PremiumEmerald)
                3 -> listOf(PremiumAmber, PremiumPink)
                4 -> listOf(PremiumPurple, PremiumIndigo)
                else -> listOf(PremiumPink, PremiumAmber)
            }
            
            CategoryGridCard(
                category = category,
                gradientColors = gradientColors,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryGridCard(
    category: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.2f) },
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.Filled.Folder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.linearGradient(colors = gradientColors),
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }
}

@Composable
fun VerbsList(
    verbs: List<KoreanVerb>,
    onVerbClick: (KoreanVerb) -> Unit,
    selectedCategory: String?,
    onClearCategory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedCategory != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category: $selectedCategory",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = onClearCategory) {
                        Text("Clear")
                    }
                }
            }
        }
        
        items(verbs, key = { it.id }) { verb ->
            VerbListItem(
                verb = verb,
                onClick = { onVerbClick(verb) }
            )
        }
        
        if (verbs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No verbs found",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerbListItem(
    verb: KoreanVerb,
    onClick: () -> Unit
) {
    PremiumCard(
        onClick = onClick,
        gradientColors = listOf(PremiumIndigo.copy(alpha = 0.3f), PremiumPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Korean Verb - full width to prevent overlap
                Text(
                    text = verb.verb,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Romanization - below Korean text, smaller
                Text(
                    text = verb.verbRomanization,
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verb.englishMeaning,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = verb.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

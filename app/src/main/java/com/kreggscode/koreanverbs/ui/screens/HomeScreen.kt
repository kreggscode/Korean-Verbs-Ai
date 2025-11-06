package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kreggscode.koreanverbs.data.repository.VerbRepository
import com.kreggscode.koreanverbs.navigation.Screen
import com.kreggscode.koreanverbs.ui.components.*
import com.kreggscode.koreanverbs.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { VerbRepository(context) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var totalVerbs by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            categories = repository.getCategories()
            totalVerbs = repository.getAllVerbs().size
            delay(500)
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Premium Header
            AnimatedHeader(navController)
            
            // Stats Cards
            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn(tween(300)) with fadeOut(tween(300))
                }
            ) { loading ->
                if (loading) {
                    LoadingShimmer()
                } else {
                    StatsSection(totalVerbs, categories.size)
                }
            }
            
            // Quick Actions
            QuickActionsSection(navController)
            
            // Category Showcase
            CategoryShowcase(categories, navController)
            
            // Featured Section
            FeaturedSection(navController)
        }
    }
}

@Composable
fun AnimatedHeader(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkMode = ThemeManager.isDarkMode
    
    val infiniteTransition = rememberInfiniteTransition()
    val animatedGradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(PremiumPurple, PremiumPink, PremiumIndigo),
                start = Offset(0f, 0f),
                end = Offset(1000f * animatedGradientOffset, 1000f * animatedGradientOffset)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PremiumIndigo.copy(alpha = 0.1f),
                            PremiumPurple.copy(alpha = 0.1f),
                            PremiumPink.copy(alpha = 0.1f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f * animatedGradientOffset, 0f)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "안녕하세요!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ready to master Korean?",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dark Mode Toggle
                    IconButton(
                        onClick = {
                            scope.launch {
                                ThemeManager.toggleDarkMode(context)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Scanner Button
                    IconButton(
                        onClick = { navController.navigate(Screen.Scanner.route) },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PremiumTeal, PremiumIndigo)
                            ),
                            shape = CircleShape
                        )
                ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSection(totalVerbs: Int, totalCategories: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total Verbs",
            value = totalVerbs.toString(),
            icon = Icons.Filled.Book,
            gradientColors = listOf(PremiumPurple, PremiumPink)
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Categories",
            value = totalCategories.toString(),
            icon = Icons.Filled.Category,
            gradientColors = listOf(PremiumIndigo, PremiumTeal)
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    gradientColors: List<Color>
) {
    GlassmorphicCard(
        modifier = modifier.height(100.dp),
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(colors = gradientColors),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Quick Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Use Column with Row for better control
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Practice Verbs",
                    icon = Icons.Filled.MenuBook,
                    gradientColors = listOf(PremiumPurple, PremiumPink),
                    onClick = { navController.navigate(Screen.Verbs.route) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Learn Hangul",
                    icon = Icons.Filled.Translate,
                    gradientColors = listOf(PremiumIndigo, PremiumTeal),
                    onClick = { navController.navigate(Screen.Hangul.route) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Take Quiz",
                    icon = Icons.Filled.Quiz,
                    gradientColors = listOf(PremiumTeal, PremiumEmerald),
                    onClick = { navController.navigate(Screen.Quiz.route) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "AI Assistant",
                    icon = Icons.Filled.Psychology,
                    gradientColors = listOf(PremiumAmber, PremiumPink),
                    onClick = { navController.navigate(Screen.AI.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
    
    PremiumCard(
        modifier = modifier
            .height(110.dp)
            .scale(scale),
        gradientColors = gradientColors,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(colors = gradientColors),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryShowcase(categories: List<String>, navController: NavController) {
    if (categories.isEmpty()) return
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Explore Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.take(5).forEachIndexed { index, category ->
                val gradientColors = when (index % 5) {
                    0 -> listOf(PremiumPurple, PremiumPink)
                    1 -> listOf(PremiumIndigo, PremiumTeal)
                    2 -> listOf(PremiumTeal, PremiumEmerald)
                    3 -> listOf(PremiumAmber, PremiumPink)
                    else -> listOf(PremiumPurple, PremiumIndigo)
                }
                
                CategoryCard(
                    category = category,
                    gradientColors = gradientColors,
                    onClick = {
                        navController.navigate(Screen.VerbCategory.createRoute(category))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.3f) },
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FeaturedSection(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Featured Learning",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        AnimatedGradientButton(
            text = "Start Daily Challenge",
            onClick = { navController.navigate(Screen.Quiz.route) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AnimatedGradientButton(
            text = "Chat with AI Tutor",
            onClick = { navController.navigate(Screen.AI.route) },
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(PremiumTeal, PremiumIndigo, PremiumPurple)
        )
    }
}

@Composable
fun LoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    ShimmerEffect()
                }
            }
        }
    }
}

package com.kreggscode.koreanverbs.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.kreggscode.koreanverbs.ui.screens.*
import com.kreggscode.koreanverbs.ui.theme.*

@androidx.camera.core.ExperimentalGetImage
@Composable
fun KoreanVerbsNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val navigationItems = listOf(
        NavigationItem.Home,
        NavigationItem.Verbs,
        NavigationItem.Hangul,
        NavigationItem.Quiz,
        NavigationItem.AI
    )
    
    // Hide navigation bar on AI chat screen for better keyboard experience
    val showBottomBar = currentDestination?.route in navigationItems.map { it.route } && 
                        currentDestination?.route != Screen.AI.route
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(Color.Transparent)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
                composable(Screen.Home.route) {
                    HomeScreen(navController = navController)
                }
                
                composable(Screen.Verbs.route) {
                    VerbsScreen(navController = navController)
                }
            
            composable(
                Screen.VerbDetail.route,
                arguments = Screen.VerbDetail.arguments
            ) { backStackEntry ->
                val verbId = backStackEntry.arguments?.getString("verbId") ?: ""
                VerbDetailScreen(verbId = verbId, navController = navController)
            }
            
            composable(
                Screen.VerbCategory.route,
                arguments = Screen.VerbCategory.arguments
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                VerbCategoryScreen(category = category, navController = navController)
            }
            
            composable(Screen.Hangul.route) {
                HangulScreen(navController = navController)
            }
            
            composable(Screen.Quiz.route) {
                QuizScreen(navController = navController)
            }
            
            composable(Screen.AI.route) {
                AIScreen(navController = navController)
            }
            
            composable(Screen.Scanner.route) {
                ScannerScreen(navController = navController)
            }
        }
        
        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            FloatingNavigationBar(
                navController = navController,
                items = navigationItems,
                currentDestination = currentDestination
            )
        }
    }
}

@Composable
fun FloatingNavigationBar(
    navController: NavController,
    items: List<NavigationItem>,
    currentDestination: androidx.navigation.NavDestination?
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val isDarkMode = isSystemInDarkTheme()
    
    // Glassmorphic floating navigation bar - only horizontal padding, no vertical padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(36.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(36.dp))
                .background(
                    color = if (isDarkMode) {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                    }
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = if (isDarkMode) {
                            listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.3f)
                            )
                        } else {
                            listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.4f)
                            )
                        },
                        start = Offset(0f, 0f),
                        end = Offset(1000f * animatedOffset, 0f)
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        item = item,
                        selected = selected,
                        onClick = {
                            if (item.route == Screen.Home.route) {
                                // Always navigate to home and clear back stack
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationBarItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val animatedColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(300)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.title,
                tint = animatedColor,
                modifier = Modifier.size(24.dp)
            )
            
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = item.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = animatedColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

val Screen.VerbDetail.arguments
    get() = listOf(
        navArgument("verbId") { type = NavType.StringType }
    )

val Screen.VerbCategory.arguments
    get() = listOf(
        navArgument("category") { type = NavType.StringType }
    )

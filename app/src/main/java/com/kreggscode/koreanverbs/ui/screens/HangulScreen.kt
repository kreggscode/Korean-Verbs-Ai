package com.kreggscode.koreanverbs.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import com.kreggscode.koreanverbs.ui.components.*
import com.kreggscode.koreanverbs.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalAnimationApi::class)

data class HangulCharacter(
    val korean: String,
    val romanization: String,
    val type: String, // "consonant" or "vowel"
    val description: String
)

@Composable
fun HangulScreen(navController: NavController) {
    val consonants = listOf(
        HangulCharacter("ㄱ", "g/k", "consonant", "like 'g' in go"),
        HangulCharacter("ㄴ", "n", "consonant", "like 'n' in no"),
        HangulCharacter("ㄷ", "d/t", "consonant", "like 'd' in do"),
        HangulCharacter("ㄹ", "r/l", "consonant", "like 'r' or 'l'"),
        HangulCharacter("ㅁ", "m", "consonant", "like 'm' in mom"),
        HangulCharacter("ㅂ", "b/p", "consonant", "like 'b' in boy"),
        HangulCharacter("ㅅ", "s", "consonant", "like 's' in sea"),
        HangulCharacter("ㅇ", "ng", "consonant", "silent or 'ng'"),
        HangulCharacter("ㅈ", "j", "consonant", "like 'j' in joy"),
        HangulCharacter("ㅊ", "ch", "consonant", "like 'ch' in chat"),
        HangulCharacter("ㅋ", "k", "consonant", "like 'k' in key"),
        HangulCharacter("ㅌ", "t", "consonant", "like 't' in tea"),
        HangulCharacter("ㅍ", "p", "consonant", "like 'p' in pie"),
        HangulCharacter("ㅎ", "h", "consonant", "like 'h' in hat")
    )
    
    val vowels = listOf(
        HangulCharacter("ㅏ", "a", "vowel", "like 'a' in father"),
        HangulCharacter("ㅑ", "ya", "vowel", "like 'ya' in yard"),
        HangulCharacter("ㅓ", "eo", "vowel", "like 'uh' in cup"),
        HangulCharacter("ㅕ", "yeo", "vowel", "like 'yuh'"),
        HangulCharacter("ㅗ", "o", "vowel", "like 'o' in go"),
        HangulCharacter("ㅛ", "yo", "vowel", "like 'yo' in yoyo"),
        HangulCharacter("ㅜ", "u", "vowel", "like 'oo' in moon"),
        HangulCharacter("ㅠ", "yu", "vowel", "like 'you'"),
        HangulCharacter("ㅡ", "eu", "vowel", "like 'u' in put"),
        HangulCharacter("ㅣ", "i", "vowel", "like 'ee' in see")
    )
    
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCharacter by remember { mutableStateOf<HangulCharacter?>(null) }
    var showTracing by remember { mutableStateOf(false) }
    val isDarkMode = isSystemInDarkTheme()
    
    // Edge-to-edge: background fills entire screen, content has padding for system bars
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            HangulHeader()
            
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    val currentTabPosition = tabPositions[selectedTab]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.BottomStart)
                            .offset(x = currentTabPosition.left)
                            .width(currentTabPosition.width)
                            .height(3.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PremiumIndigo, PremiumPurple)
                                ),
                                shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                            )
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Consonants", fontWeight = FontWeight.Medium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Vowels", fontWeight = FontWeight.Medium) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Character Grid
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                }
            ) { tab ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val characters = if (tab == 0) consonants else vowels
                    items(characters) { character ->
                        HangulCharacterCard(
                            character = character,
                            onClick = {
                                selectedCharacter = character
                                showTracing = true
                            }
                        )
                    }
                }
            }
        }
        
        // Tracing Dialog
        if (showTracing && selectedCharacter != null) {
            TracingDialog(
                character = selectedCharacter!!,
                onDismiss = { 
                    showTracing = false
                    selectedCharacter = null
                }
            )
        }
    }
}

@Composable
fun HangulHeader() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
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
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(PremiumTeal, PremiumIndigo, PremiumPurple),
                start = Offset(0f, 0f),
                end = Offset(1000f * animatedOffset, 1000f * animatedOffset)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PremiumTeal.copy(alpha = 0.1f),
                            PremiumIndigo.copy(alpha = 0.1f),
                            PremiumPurple.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "한글",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumIndigo
                )
                Text(
                    text = "Learn Korean Alphabet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap any character to practice tracing",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HangulCharacterCard(
    character: HangulCharacter,
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
            .aspectRatio(1f)
            .scale(scale),
        onClick = onClick,
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (character.type == "consonant") 
                                PremiumIndigo.copy(alpha = 0.05f)
                            else 
                                PremiumTeal.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = character.korean,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (character.type == "consonant") PremiumIndigo else PremiumTeal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = character.romanization,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracingDialog(
    character: HangulCharacter,
    onDismiss: () -> Unit
) {
    // Single list of paths for instant rendering
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var redrawTrigger by remember { mutableStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Practice Tracing",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Character Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = character.korean,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumIndigo
                        )
                        Text(
                            text = character.romanization,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sound",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = character.description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Drawing Canvas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(2.dp, PremiumIndigo.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Background character guide
                        Text(
                            text = character.korean,
                            fontSize = 200.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            val newPath = Path().apply {
                                                moveTo(offset.x, offset.y)
                                            }
                                            currentPath = newPath
                                            paths.add(newPath)
                                            redrawTrigger++
                                        },
                                        onDrag = { change, _ ->
                                            currentPath?.lineTo(change.position.x, change.position.y)
                                            redrawTrigger++
                                        },
                                        onDragEnd = {
                                            currentPath = null
                                            redrawTrigger++
                                        }
                                    )
                                }
                        ) {
                            // Force redraw by reading redrawTrigger
                            redrawTrigger
                            // Draw all paths
                            paths.forEach { path ->
                                drawPath(
                                    path = path,
                                    color = PremiumIndigo,
                                    style = Stroke(
                                        width = 12.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            paths.clear()
                            currentPath = null
                            redrawTrigger++
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear")
                    }
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumIndigo
                        )
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done")
                    }
                }
            }
        }
    }
}

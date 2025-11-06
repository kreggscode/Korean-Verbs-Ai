package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kreggscode.koreanverbs.data.models.KoreanVerb
import com.kreggscode.koreanverbs.data.repository.VerbRepository
import com.kreggscode.koreanverbs.ui.components.*
import com.kreggscode.koreanverbs.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class QuizType {
    MULTIPLE_CHOICE, FLASHCARD, TIME_CHALLENGE, TRUE_FALSE
}

enum class QuizDifficulty {
    EASY, MEDIUM, HARD
}

data class QuizQuestion(
    val verb: KoreanVerb,
    val options: List<String>,
    val correctAnswer: String
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { VerbRepository(context) }
    val scope = rememberCoroutineScope()
    
    var selectedQuizType by remember { mutableStateOf<QuizType?>(null) }
    var selectedDifficulty by remember { mutableStateOf(QuizDifficulty.EASY) }
    var isQuizActive by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(10) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        AnimatedContent(
            targetState = isQuizActive,
            transitionSpec = {
                fadeIn(tween(400)) + slideInHorizontally(tween(400)) with
                fadeOut(tween(200)) + slideOutHorizontally(tween(200))
            }
        ) { quizActive ->
            if (!quizActive || selectedQuizType == null) {
                QuizSelectionScreen(
                    onQuizTypeSelected = { type ->
                        selectedQuizType = type
                        isQuizActive = true
                    },
                    onDifficultyChanged = { selectedDifficulty = it },
                    currentDifficulty = selectedDifficulty,
                    navController = navController
                )
            } else {
                when (selectedQuizType) {
                    QuizType.MULTIPLE_CHOICE -> MultipleChoiceQuiz(
                        repository = repository,
                        difficulty = selectedDifficulty,
                        onBackClick = { 
                            isQuizActive = false
                            selectedQuizType = null
                        },
                        onScoreUpdate = { score = it }
                    )
                    QuizType.FLASHCARD -> FlashcardQuiz(
                        repository = repository,
                        onBackClick = { 
                            isQuizActive = false
                            selectedQuizType = null
                        }
                    )
                    QuizType.TIME_CHALLENGE -> TimeChallengeQuiz(
                        repository = repository,
                        difficulty = selectedDifficulty,
                        onBackClick = { 
                            isQuizActive = false
                            selectedQuizType = null
                        },
                        onScoreUpdate = { score = it }
                    )
                    QuizType.TRUE_FALSE -> TrueFalseQuiz(
                        repository = repository,
                        onBackClick = { 
                            isQuizActive = false
                            selectedQuizType = null
                        },
                        onScoreUpdate = { score = it }
                    )
                    null -> {}
                }
            }
        }
    }
}

@Composable
fun QuizSelectionScreen(
    onQuizTypeSelected: (QuizType) -> Unit,
    onDifficultyChanged: (QuizDifficulty) -> Unit,
    currentDifficulty: QuizDifficulty,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Header
        QuizHeader()
        
        // Difficulty Selector
        DifficultySelector(
            currentDifficulty = currentDifficulty,
            onDifficultyChanged = onDifficultyChanged
        )
        
        // Quiz Types Grid
        QuizTypesGrid(onQuizTypeSelected)
        
        // Stats Card
        QuizStatsCard()
    }
}

@Composable
fun QuizHeader() {
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
                colors = listOf(PremiumPurple, PremiumPink, PremiumIndigo),
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
                            PremiumIndigo.copy(alpha = 0.1f),
                            PremiumPurple.copy(alpha = 0.1f),
                            PremiumPink.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Quiz,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PremiumPurple, PremiumPink)
                            ),
                            shape = CircleShape
                        )
                        .padding(12.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Test Your Knowledge",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose a quiz type to start learning",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DifficultySelector(
    currentDifficulty: QuizDifficulty,
    onDifficultyChanged: (QuizDifficulty) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Select Difficulty",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuizDifficulty.values().forEach { difficulty ->
                val selected = currentDifficulty == difficulty
                val colors = when (difficulty) {
                    QuizDifficulty.EASY -> listOf(PremiumEmerald, PremiumTeal)
                    QuizDifficulty.MEDIUM -> listOf(PremiumAmber, PremiumPink)
                    QuizDifficulty.HARD -> listOf(PremiumPink, Color.Red)
                }
                
                FilterChip(
                    selected = selected,
                    onClick = { onDifficultyChanged(difficulty) },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(
                            difficulty.name.lowercase().capitalize(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors[0].copy(alpha = 0.2f),
                        selectedLabelColor = colors[0]
                    ),
                    border = if (selected) {
                        FilterChipDefaults.filterChipBorder(
                            borderColor = colors[0],
                            selectedBorderColor = colors[0],
                            borderWidth = 2.dp,
                            selectedBorderWidth = 2.dp
                        )
                    } else null
                )
            }
        }
    }
}

@Composable
fun QuizTypesGrid(onQuizTypeSelected: (QuizType) -> Unit) {
    val quizTypes = listOf(
        Triple(QuizType.MULTIPLE_CHOICE, "Multiple Choice", Icons.Filled.RadioButtonChecked),
        Triple(QuizType.FLASHCARD, "Flashcards", Icons.Filled.Style),
        Triple(QuizType.TIME_CHALLENGE, "Time Challenge", Icons.Filled.Timer),
        Triple(QuizType.TRUE_FALSE, "True or False", Icons.Filled.CheckCircle)
    )
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Quiz Types",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        quizTypes.forEachIndexed { index, (type, title, icon) ->
            val gradientColors = when (index) {
                0 -> listOf(PremiumPurple, PremiumPink)
                1 -> listOf(PremiumIndigo, PremiumTeal)
                2 -> listOf(PremiumAmber, PremiumPink)
                else -> listOf(PremiumTeal, PremiumEmerald)
            }
            
            QuizTypeCard(
                title = title,
                icon = icon,
                gradientColors = gradientColors,
                onClick = { onQuizTypeSelected(type) },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun QuizTypeCard(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.1f) }
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
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
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun QuizStatsCard() {
    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        gradientColors = listOf(PremiumIndigo.copy(alpha = 0.3f), PremiumPurple.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Quizzes Taken", value = "0", icon = Icons.Filled.Assignment)
                StatItem(label = "Best Score", value = "0%", icon = Icons.Filled.Star)
                StatItem(label = "Streak", value = "0", icon = Icons.Filled.LocalFireDepartment)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AnimatedProgressBar(
                progress = 0f,
                modifier = Modifier.fillMaxWidth(),
                height = 12.dp,
                gradientColors = listOf(PremiumIndigo, PremiumPurple, PremiumPink)
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PremiumIndigo,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MultipleChoiceQuiz(
    repository: VerbRepository,
    difficulty: QuizDifficulty,
    onBackClick: () -> Unit,
    onScoreUpdate: (Int) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            val verbs = repository.getRandomVerbs(10)
            questions = verbs.map { verb ->
                val allVerbs = repository.getAllVerbs()
                val wrongAnswers = allVerbs
                    .filter { it.id != verb.id }
                    .shuffled()
                    .take(3)
                    .map { it.englishMeaning }
                
                val options = (wrongAnswers + verb.englishMeaning).shuffled()
                QuizQuestion(verb, options, verb.englishMeaning)
            }
            isLoading = false
        }
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PremiumIndigo)
        }
    } else if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
        val currentQuestion = questions[currentQuestionIndex]
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 120.dp)
        ) {
            // Quiz Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Score: $score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumIndigo
                )
            }
            
            // Progress Bar
            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1) / questions.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = PremiumIndigo,
                trackColor = PremiumIndigo.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Question Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "What does this mean?",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = currentQuestion.verb.verb,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumIndigo
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = currentQuestion.verb.verbRomanization,
                        fontSize = 18.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Answer Options
            currentQuestion.options.forEach { option ->
                val isCorrect = showResult && option == currentQuestion.correctAnswer
                val isWrong = showResult && option == selectedAnswer && option != currentQuestion.correctAnswer
                val isSelected = option == selectedAnswer
                
                AnswerOptionCard(
                    text = option,
                    isSelected = isSelected,
                    isCorrect = isCorrect,
                    isWrong = isWrong,
                    enabled = !showResult,
                    onClick = {
                        selectedAnswer = option
                        showResult = true
                        if (option == currentQuestion.correctAnswer) {
                            score++
                            onScoreUpdate(score)
                        }
                        
                        scope.launch {
                            delay(1500)
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedAnswer = null
                                showResult = false
                            } else {
                                // Quiz completed
                                onBackClick()
                            }
                        }
                    },
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AnswerOptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCorrect -> PremiumEmerald.copy(alpha = 0.2f)
        isWrong -> Error.copy(alpha = 0.2f)
        isSelected -> PremiumIndigo.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    
    val borderColor = when {
        isCorrect -> PremiumEmerald
        isWrong -> Error
        isSelected -> PremiumIndigo
        else -> Color.Transparent
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { if (enabled) onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (borderColor != Color.Transparent) {
            BorderStroke(2.dp, borderColor)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (isCorrect) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = PremiumEmerald
                )
            } else if (isWrong) {
                Icon(
                    Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = Error
                )
            }
        }
    }
}

// Simplified versions of other quiz types (you can expand these)
@Composable
fun FlashcardQuiz(
    repository: VerbRepository,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var verbs by remember { mutableStateOf<List<KoreanVerb>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            verbs = repository.getAllVerbs().shuffled().take(20)
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
                .padding(16.dp)
                .padding(bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${currentIndex + 1} / ${verbs.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (verbs.isNotEmpty() && currentIndex < verbs.size) {
                val verb = verbs[currentIndex]
                
                // Flashcard
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { showAnswer = !showAnswer },
                    cornerRadius = 32.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = showAnswer,
                            transitionSpec = {
                                fadeIn() + scaleIn() with fadeOut() + scaleOut()
                            }
                        ) { showing ->
                            if (!showing) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = verb.verb,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PremiumIndigo,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 56.sp,
                                        maxLines = 3,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Tap to reveal",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = verb.englishMeaning,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PremiumPink,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 38.sp,
                                        maxLines = 3,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = verb.koreanSentence,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 26.sp,
                                        maxLines = 4,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = verb.englishSentence,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        maxLines = 3,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                showAnswer = false
                            }
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumIndigo
                        )
                    ) {
                        Icon(Icons.Filled.ChevronLeft, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }
                    
                    Button(
                        onClick = {
                            if (currentIndex < verbs.size - 1) {
                                currentIndex++
                                showAnswer = false
                            } else {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumPurple
                        )
                    ) {
                        Text(if (currentIndex < verbs.size - 1) "Next" else "Finish")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.ChevronRight, null)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeChallengeQuiz(
    repository: VerbRepository,
    difficulty: QuizDifficulty,
    onBackClick: () -> Unit,
    onScoreUpdate: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(60) } // 60 seconds
    var isGameOver by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            val verbs = repository.getAllVerbs().shuffled().take(15)
            questions = verbs.map { verb ->
                val allVerbs = repository.getAllVerbs()
                val wrongAnswers = allVerbs.filter { it.id != verb.id }
                    .shuffled()
                    .take(3)
                    .map { it.englishMeaning }
                val options = (wrongAnswers + verb.englishMeaning).shuffled()
                QuizQuestion(verb, options, verb.englishMeaning)
            }
        }
    }
    
    // Timer
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0 && !isGameOver) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0) {
            isGameOver = true
            onScoreUpdate(score)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        if (isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Time's Up!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumAmber
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Score: $score / ${questions.size}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumAmber)
                ) {
                    Text("Back to Menu")
                }
            }
        } else if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 120.dp)
            ) {
                // Header with timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (timeLeft <= 10) PremiumPink else PremiumAmber
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Timer, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$timeLeft s",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val question = questions[currentQuestionIndex]
                
                // Question
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "What does this mean?",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = question.verb.verb,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumIndigo
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.verb.verbRomanization,
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Options
                question.options.forEach { option ->
                    Button(
                        onClick = {
                            if (option == question.correctAnswer) {
                                score++
                                onScoreUpdate(score)
                            }
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                isGameOver = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = option,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrueFalseQuiz(
    repository: VerbRepository,
    onBackClick: () -> Unit,
    onScoreUpdate: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var verbs by remember { mutableStateOf<List<KoreanVerb>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var isTrue by remember { mutableStateOf(true) }
    var displayedMeaning by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        scope.launch {
            verbs = repository.getAllVerbs().shuffled().take(15)
        }
    }
    
    LaunchedEffect(currentIndex) {
        if (verbs.isNotEmpty() && currentIndex < verbs.size) {
            val verb = verbs[currentIndex]
            val random = kotlin.random.Random.nextBoolean()
            isTrue = random
            displayedMeaning = if (random) {
                verb.englishMeaning
            } else {
                val wrongVerb = verbs.filter { it.id != verb.id }.random()
                wrongVerb.englishMeaning
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        if (currentIndex >= verbs.size && verbs.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Quiz Complete!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumTeal
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Score: $score / ${verbs.size}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumTeal)
                ) {
                    Text("Back to Menu")
                }
            }
        } else if (verbs.isNotEmpty() && currentIndex < verbs.size) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 100.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${currentIndex + 1} / ${verbs.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                val verb = verbs[currentIndex]
                
                // Question Card
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = verb.verb,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumIndigo,
                            textAlign = TextAlign.Center,
                            lineHeight = 64.sp,
                            maxLines = 2,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "means",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = displayedMeaning,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PremiumPink,
                            textAlign = TextAlign.Center,
                            lineHeight = 40.sp,
                            maxLines = 3,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        if (showFeedback) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCorrect) PremiumEmerald else PremiumPink
                                )
                            ) {
                                Text(
                                    text = if (isCorrect) "✓ Correct!" else "✗ Wrong!",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // True/False Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val correct = isTrue
                            isCorrect = correct
                            if (correct) {
                                score++
                                onScoreUpdate(score)
                            }
                            showFeedback = true
                            scope.launch {
                                delay(1000)
                                showFeedback = false
                                currentIndex++
                            }
                        },
                        enabled = !showFeedback,
                        modifier = Modifier.weight(1f).height(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumEmerald
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Check, null, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TRUE", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        onClick = {
                            val correct = !isTrue
                            isCorrect = correct
                            if (correct) {
                                score++
                                onScoreUpdate(score)
                            }
                            showFeedback = true
                            scope.launch {
                                delay(1000)
                                showFeedback = false
                                currentIndex++
                            }
                        },
                        enabled = !showFeedback,
                        modifier = Modifier.weight(1f).height(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumPink
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Close, null, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("FALSE", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

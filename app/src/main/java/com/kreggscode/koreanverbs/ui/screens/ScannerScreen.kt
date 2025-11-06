package com.kreggscode.koreanverbs.ui.screens

import android.Manifest
import android.speech.tts.TextToSpeech
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kreggscode.koreanverbs.ui.components.*
import com.kreggscode.koreanverbs.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@androidx.camera.core.ExperimentalGetImage
@Composable
fun ScannerScreen(navController: NavController) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                CameraView(navController)
            }
            cameraPermissionState.status.shouldShowRationale -> {
                PermissionRationale(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onCancel = { navController.navigateUp() }
                )
            }
            else -> {
                PermissionDenied(
                    onGoBack = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
fun CameraView(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var detectedText by remember { mutableStateOf("") }
    var detectedLabels by remember { mutableStateOf<List<String>>(emptyList()) }
    var koreanTranslation by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var scanMode by remember { mutableStateOf(ScanMode.OBJECT) }
    var showResult by remember { mutableStateOf(false) }
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale.KOREAN)
            }
        }
        
        onDispose {
            tts?.stop()
            tts?.shutdown()
            cameraExecutor.shutdown()
        }
    }
    
    // Remember the analyzer to update it dynamically
    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }
    
    // Real-time continuous scanning
    var lastProcessedTime by remember { mutableStateOf(0L) }
    val scanInterval = 500L // Process every 500ms for real-time scanning
    
    LaunchedEffect(scanMode, showResult) {
        // Only scan continuously when not showing result
        if (!showResult) {
            imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastProcessedTime >= scanInterval && !isProcessing) {
                    lastProcessedTime = currentTime
                    isProcessing = true
                    
                    // Process the image
                    processImage(
                        imageProxy = imageProxy,
                        scanMode = scanMode,
                        onTextDetected = { text ->
                            if (text.isNotEmpty()) {
                                detectedText = text
                                koreanTranslation = translateToKorean(text)
                                showResult = true
                            }
                            isProcessing = false
                        },
                        onLabelsDetected = { labels ->
                            if (labels.isNotEmpty()) {
                                detectedLabels = labels
                                val label = labels.first()
                                koreanTranslation = translateToKorean(label)
                                showResult = true
                            }
                            isProcessing = false
                        }
                    )
                }
                imageProxy.close()
            }
        } else {
            // Discard frames when showing result
            imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                imageProxy.close()
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Overlay UI
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            ScannerTopBar(
                onBackClick = { navController.navigateUp() },
                scanMode = scanMode,
                onModeChange = { scanMode = it }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Capture/Reset Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (showResult) {
                            // Reset and continue scanning
                            showResult = false
                            detectedText = ""
                            detectedLabels = emptyList()
                            koreanTranslation = ""
                            lastProcessedTime = 0L // Reset timer for immediate scan
                        }
                        // Note: Real-time scanning is now automatic, no need to capture manually
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PremiumIndigo, PremiumPurple)
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        if (showResult) Icons.Filled.Refresh else Icons.Filled.Camera,
                        contentDescription = if (showResult) "Reset" else "Scanning",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        
        // Scanning Animation
        AnimatedVisibility(
            visible = isProcessing,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = PremiumIndigo,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Processing image...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        // Result Card
        AnimatedVisibility(
            visible = showResult && !isProcessing,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ScanResultCard(
                detectedText = if (scanMode == ScanMode.TEXT) detectedText else detectedLabels.firstOrNull() ?: "",
                englishText = if (scanMode == ScanMode.TEXT) detectedText else detectedLabels.firstOrNull() ?: "",
                koreanTranslation = koreanTranslation,
                onSpeak = {
                    tts?.setLanguage(Locale.KOREAN)
                    tts?.speak(koreanTranslation, TextToSpeech.QUEUE_FLUSH, null, null)
                },
                onDismiss = { showResult = false }
            )
        }
    }
}

enum class ScanMode {
    OBJECT, TEXT
}

@Composable
fun ScannerTopBar(
    onBackClick: () -> Unit,
    scanMode: ScanMode,
    onModeChange: (ScanMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "Object Scanner",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(onClick = { /* Settings */ }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = scanMode == ScanMode.OBJECT,
                    onClick = { onModeChange(ScanMode.OBJECT) },
                    label = { Text("Object Detection") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = scanMode == ScanMode.TEXT,
                    onClick = { onModeChange(ScanMode.TEXT) },
                    label = { Text("Text Recognition") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ScanResultCard(
    detectedText: String,
    englishText: String,
    koreanTranslation: String,
    onSpeak: () -> Unit,
    onDismiss: () -> Unit
) {
    // Generate romanization
    val romanization = getRomanization(koreanTranslation)
    
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PremiumIndigo.copy(alpha = 0.1f),
                            PremiumPurple.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detected Object",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // English Text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "English",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = englishText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PremiumPink
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Korean Translation with Romanization
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Korean (한국어)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = koreanTranslation,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumIndigo
                    )
                    if (romanization.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "($romanization)",
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            PremiumIndigo.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Speak",
                        tint = PremiumIndigo
                    )
                }
            }
        }
    }
}

// Simple romanization helper - maps common Korean words
fun getRomanization(korean: String): String {
    val romanizationMap = mapOf(
        "사과" to "sagwa",
        "물" to "mul",
        "책" to "chaek",
        "컴퓨터" to "keompyuteo",
        "전화" to "jeonhwa",
        "의자" to "uija",
        "테이블" to "teibeul",
        "문" to "mun",
        "창문" to "changmun",
        "자동차" to "jadongcha",
        "나무" to "namu",
        "꽃" to "kkot",
        "고양이" to "goyangi",
        "개" to "gae",
        "사람" to "saram"
    )
    return romanizationMap[korean] ?: ""
}

@Composable
fun PermissionRationale(
    onRequestPermission: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PremiumIndigo
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Camera Permission Required",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This feature needs camera access to scan objects and translate them to Korean.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AnimatedGradientButton(
            text = "Grant Permission",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(onClick = onCancel) {
            Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun PermissionDenied(
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Camera Permission Denied",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Please enable camera permission in your device settings to use this feature.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedButton(
            onClick = onGoBack,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Go Back")
        }
    }
}

// ML Kit Processing Functions
@androidx.camera.core.ExperimentalGetImage
fun processImage(
    imageProxy: ImageProxy,
    scanMode: ScanMode,
    onTextDetected: (String) -> Unit,
    onLabelsDetected: (List<String>) -> Unit
) {
    try {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            when (scanMode) {
                ScanMode.TEXT -> {
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            onTextDetected(visionText.text)
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
                ScanMode.OBJECT -> {
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            onLabelsDetected(labels.map { it.text })
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Simple translation function (you can expand this)
fun translateToKorean(englishWord: String): String {
    return when (englishWord.lowercase()) {
        "person", "human" -> "사람"
        "cat" -> "고양이"
        "dog" -> "개"
        "car" -> "자동차"
        "phone", "mobile" -> "휴대폰"
        "computer" -> "컴퓨터"
        "book" -> "책"
        "table" -> "테이블"
        "chair" -> "의자"
        "door" -> "문"
        "window" -> "창문"
        "tree" -> "나무"
        "flower" -> "꽃"
        "water" -> "물"
        "food" -> "음식"
        "coffee" -> "커피"
        "house", "home" -> "집"
        "school" -> "학교"
        "pen" -> "펜"
        "paper" -> "종이"
        else -> englishWord // Return original if no translation found
    }
}

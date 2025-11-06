package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kreggscode.koreanverbs.ui.components.GlassmorphicCard
import com.kreggscode.koreanverbs.ui.theme.*

enum class CultureTab {
    OVERVIEW, FESTIVALS, TRADITIONS, INTERESTING_FACTS, ETIQUETTE
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun KoreanCultureScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Festivals", "Traditions", "Facts", "Etiquette")
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface // Visible in both light and dark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Korean Culture",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Scrollable Tabs - prevents text truncation on small screens
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                            ) 
                        }
                    )
                }
            }
            
            // Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                }
            ) { tab ->
                when (tab) {
                    0 -> CultureOverviewContent()
                    1 -> FestivalsContent()
                    2 -> TraditionsContent()
                    3 -> InterestingFactsContent()
                    4 -> EtiquetteContent()
                }
            }
        }
    }
}

@Composable
fun CultureOverviewContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CultureCard(
                title = "한국 문화 (Hanguk Munhwa)",
                subtitle = "Korean Culture",
                description = "Korean culture is rich with traditions dating back thousands of years, blending ancient customs with modern innovations.",
                gradientColors = listOf(PremiumPurple, PremiumIndigo)
            )
        }
        
        item {
            CultureInfoCard(
                title = "Historical Background",
                items = listOf(
                    "Over 5,000 years of history",
                    "Three Kingdoms period (57 BC - 668 AD)",
                    "Joseon Dynasty (1392-1897)",
                    "Modern Korea (1948-present)"
                )
            )
        }
        
        item {
            CultureInfoCard(
                title = "Core Values",
                items = listOf(
                    "Respect for elders (경로효친)",
                    "Harmony and balance",
                    "Education and hard work",
                    "Family bonds (가족)"
                )
            )
        }
    }
}

@Composable
fun FestivalsContent() {
    val festivals = listOf(
        FestivalInfo("설날 (Seollal)", "Lunar New Year", "Most important holiday, family gatherings, ancestral rites"),
        FestivalInfo("추석 (Chuseok)", "Harvest Festival", "Thanksgiving, family reunions, traditional games"),
        FestivalInfo("단오 (Dano)", "Spring Festival", "Traditional wrestling, swing riding, herbal baths"),
        FestivalInfo("불꽃놀이 (Bulgwolnori)", "Fireworks Festival", "Various locations, spectacular displays"),
        FestivalInfo("정월대보름 (Jeongwol Daeboreum)", "First Full Moon", "Wish-making, nut cracking, moon viewing")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(festivals) { festival ->
            FestivalCard(festival = festival)
        }
    }
}

@Composable
fun TraditionsContent() {
    val traditions = listOf(
        TraditionInfo("한복 (Hanbok)", "Traditional clothing worn on special occasions", "Vibrant colors, elegant design"),
        TraditionInfo("차례 (Charye)", "Ancestral memorial rites", "Respect for ancestors, traditional foods"),
        TraditionInfo("세배 (Sebae)", "New Year's bow", "Deep bow to elders, receiving blessings"),
        TraditionInfo("돌잔치 (Doljanchi)", "First birthday celebration", "Doljabi ceremony, predicting child's future"),
        TraditionInfo("혼례 (Honrye)", "Traditional wedding", "Pyebaek ceremony, family union")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(traditions) { tradition ->
            TraditionCard(tradition = tradition)
        }
    }
}

@Composable
fun InterestingFactsContent() {
    val facts = listOf(
        "Korea has one of the fastest internet speeds in the world",
        "Age is calculated differently - you're 1 year old at birth",
        "Blood type is considered important in relationships",
        "Fan death is a widely believed urban legend",
        "Korea has the highest rate of plastic surgery per capita",
        "Bowing is still a common greeting",
        "Shoes are always removed before entering homes",
        "Red ink is never used to write names (bad luck)",
        "The number 4 is considered unlucky",
        "Korea has 4 distinct seasons with beautiful transitions"
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(facts) { fact ->
            FactCard(fact = fact)
        }
    }
}

@Composable
fun EtiquetteContent() {
    val etiquetteRules = listOf(
        EtiquetteRule("Greetings", "Bow when greeting, especially to elders. Handshakes are common in business."),
        EtiquetteRule("Dining", "Wait for the eldest to start eating. Never stick chopsticks upright in rice."),
        EtiquetteRule("Gifts", "Give and receive with both hands. Gifts are important in relationships."),
        EtiquetteRule("Respect", "Use honorifics when speaking to elders. Age hierarchy is important."),
        EtiquetteRule("Public Behavior", "Keep voices low in public. Personal space is smaller than in the West.")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(etiquetteRules) { rule ->
            EtiquetteCard(rule = rule)
        }
    }
}

@Composable
fun CultureCard(
    title: String,
    subtitle: String,
    description: String,
    gradientColors: List<Color>
) {
    GlassmorphicCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CultureInfoCard(title: String, items: List<String>) {
    GlassmorphicCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 16.sp,
                        color = PremiumPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

data class FestivalInfo(val korean: String, val english: String, val description: String)

@Composable
fun FestivalCard(festival: FestivalInfo) {
    GlassmorphicCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = festival.korean,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = festival.english,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = festival.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

data class TraditionInfo(val name: String, val description: String, val details: String)

@Composable
fun TraditionCard(tradition: TraditionInfo) {
    GlassmorphicCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = tradition.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tradition.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tradition.details,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun FactCard(fact: String) {
    GlassmorphicCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = PremiumAmber,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = fact,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f),
                lineHeight = 20.sp
            )
        }
    }
}

data class EtiquetteRule(val title: String, val description: String)

@Composable
fun EtiquetteCard(rule: EtiquetteRule) {
    GlassmorphicCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = PremiumIndigo,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = rule.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = rule.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}


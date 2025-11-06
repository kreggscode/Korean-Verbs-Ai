package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kreggscode.koreanverbs.ui.components.GlassmorphicCard
import com.kreggscode.koreanverbs.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BasicGrammarScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Basics", "Particles", "Verb Endings", "Honorifics", "Tenses")

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
                            androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Basic Grammar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Scrollable Tabs
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
                    fadeIn(animationSpec = tween(300)) + slideInHorizontally() togetherWith
                    fadeOut(animationSpec = tween(300)) + slideOutHorizontally()
                },
                label = "grammar_tab_content"
            ) { tab: Int ->
                when (tab) {
                    0 -> GrammarBasicsContent()
                    1 -> ParticlesContent()
                    2 -> VerbEndingsContent()
                    3 -> HonorificsContent()
                    4 -> TensesContent()
                    else -> GrammarBasicsContent()
                }
            }
        }
    }
}

@Composable
fun GrammarBasicsContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GrammarCard(
                title = "Word Order",
                content = "Korean follows Subject-Object-Verb (SOV) order, unlike English's SVO.\n\n" +
                        "Example:\n" +
                        "• English: I eat an apple\n" +
                        "• Korean: 나는 사과를 먹어요 (I apple eat)"
            )
        }
        item {
            GrammarCard(
                title = "No Articles",
                content = "Korean doesn't use articles like 'a', 'an', or 'the'.\n\n" +
                        "Example:\n" +
                        "• 사과 (apple) - not 'an apple' or 'the apple'"
            )
        }
        item {
            GrammarCard(
                title = "Topic Markers",
                content = "은/는 (eun/neun) marks the topic of the sentence.\n\n" +
                        "• Use 은 after consonants: 책은 (the book)\n" +
                        "• Use 는 after vowels: 나는 (I)"
            )
        }
        item {
            GrammarCard(
                title = "Subject Markers",
                content = "이/가 (i/ga) marks the subject.\n\n" +
                        "• Use 이 after consonants: 친구가 (friend)\n" +
                        "• Use 가 after vowels: 엄마가 (mom)"
            )
        }
    }
}

@Composable
fun ParticlesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GrammarCard(
                title = "을/를 (Object Marker)",
                content = "Marks the direct object of a verb.\n\n" +
                        "• Use 을 after consonants: 책을 읽어요 (read a book)\n" +
                        "• Use 를 after vowels: 사과를 먹어요 (eat an apple)"
            )
        }
        item {
            GrammarCard(
                title = "에 (Location/Time)",
                content = "Marks location or time.\n\n" +
                        "• Location: 학교에 가요 (go to school)\n" +
                        "• Time: 아침에 (in the morning)"
            )
        }
        item {
            GrammarCard(
                title = "에서 (From/At)",
                content = "Marks the location where an action occurs.\n\n" +
                        "• 집에서 공부해요 (study at home)\n" +
                        "• 한국에서 왔어요 (came from Korea)"
            )
        }
        item {
            GrammarCard(
                title = "와/과 (And)",
                content = "Connects nouns meaning 'and'.\n\n" +
                        "• Use 와 after vowels: 친구와 (with friend)\n" +
                        "• Use 과 after consonants: 책과 (with book)"
            )
        }
        item {
            GrammarCard(
                title = "의 (Possessive)",
                content = "Shows possession, like 's in English.\n\n" +
                        "• 나의 책 (my book) or 내 책 (my book - shortened)"
            )
        }
    }
}

@Composable
fun VerbEndingsContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GrammarCard(
                title = "아요/어요 (Polite)",
                content = "Polite present tense ending.\n\n" +
                        "• 가다 (to go) → 가요\n" +
                        "• 먹다 (to eat) → 먹어요\n" +
                        "• 하다 (to do) → 해요"
            )
        }
        item {
            GrammarCard(
                title = "습니다/ㅂ니다 (Formal)",
                content = "Very formal ending.\n\n" +
                        "• 가다 → 갑니다\n" +
                        "• 먹다 → 먹습니다\n" +
                        "• 하다 → 합니다"
            )
        }
        item {
            GrammarCard(
                title = "아/어 (Casual)",
                content = "Casual ending for friends.\n\n" +
                        "• 가다 → 가\n" +
                        "• 먹다 → 먹어\n" +
                        "• 하다 → 해"
            )
        }
        item {
            GrammarCard(
                title = "Verb Stem Rules",
                content = "• If last vowel is ㅏ or ㅗ → use 아요\n" +
                        "• If last vowel is anything else → use 어요\n" +
                        "• 하다 verbs → 해요"
            )
        }
    }
}

@Composable
fun HonorificsContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GrammarCard(
                title = "시 (Honorific Suffix)",
                content = "Add 시 to verb stem to show respect.\n\n" +
                        "• 가다 → 가시다 (go - honorific)\n" +
                        "• 먹다 → 드시다 (eat - honorific form changes)\n" +
                        "• 주다 → 주시다 (give - honorific)"
            )
        }
        item {
            GrammarCard(
                title = "Honorific Verbs",
                content = "Some verbs have special honorific forms.\n\n" +
                        "• 먹다 → 드시다 (eat)\n" +
                        "• 자다 → 주무시다 (sleep)\n" +
                        "• 있다 → 계시다 (be/exist)"
            )
        }
        item {
            GrammarCard(
                title = "When to Use",
                content = "Use honorifics when:\n" +
                        "• Speaking to elders\n" +
                        "• In formal situations\n" +
                        "• Showing respect to strangers\n" +
                        "• In business settings"
            )
        }
        item {
            GrammarCard(
                title = "Casual vs Polite",
                content = "• Casual: 친구에게 말해 (tell a friend)\n" +
                        "• Polite: 선생님께 말씀드려요 (tell teacher)\n" +
                        "• Formal: 교수님께 말씀드립니다 (tell professor)"
            )
        }
    }
}

@Composable
fun TensesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GrammarCard(
                title = "Present Tense",
                content = "아요/어요 or 합니다 form.\n\n" +
                        "• 가요 (go)\n" +
                        "• 먹어요 (eat)\n" +
                        "• 해요 (do)"
            )
        }
        item {
            GrammarCard(
                title = "Past Tense",
                content = "Add 았/었/했 to verb stem.\n\n" +
                        "• 가다 → 갔어요 (went)\n" +
                        "• 먹다 → 먹었어요 (ate)\n" +
                        "• 하다 → 했어요 (did)"
            )
        }
        item {
            GrammarCard(
                title = "Future Tense",
                content = "Use (으)ㄹ 거예요 or 겠어요.\n\n" +
                        "• 갈 거예요 (will go)\n" +
                        "• 먹을 거예요 (will eat)\n" +
                        "• 할 거예요 (will do)"
            )
        }
        item {
            GrammarCard(
                title = "Past Tense Rules",
                content = "• If last vowel is ㅏ or ㅗ → 았어요\n" +
                        "• Otherwise → 었어요\n" +
                        "• 하다 → 했어요"
            )
        }
    }
}

@Composable
fun GrammarCard(title: String, content: String) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PremiumIndigo
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}


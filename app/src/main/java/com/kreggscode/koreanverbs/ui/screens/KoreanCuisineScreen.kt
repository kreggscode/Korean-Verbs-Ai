package com.kreggscode.koreanverbs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun KoreanCuisineScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Popular", "Famous", "Regular", "Likes", "Dislikes")
    
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
                    text = "Korean Cuisine",
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
                    fadeIn(animationSpec = tween(300)) + slideInHorizontally() togetherWith 
                    fadeOut(animationSpec = tween(300)) + slideOutHorizontally()
                },
                label = "tab_content"
            ) { tab: Int ->
                when (tab) {
                    0 -> PopularFoodsContent()
                    1 -> FamousFoodsContent()
                    2 -> RegularFoodsContent()
                    3 -> LikedFoodsContent()
                    4 -> DislikedFoodsContent()
                    else -> PopularFoodsContent()
                }
            }
        }
    }
}

@Composable
fun PopularFoodsContent() {
    val popularFoods = listOf(
        FoodInfo("김치 (Kimchi)", "Fermented vegetables", "National dish, served with every meal", "Spicy, tangy, probiotic"),
        FoodInfo("불고기 (Bulgogi)", "Marinated beef", "Grilled meat, sweet and savory", "Tender, flavorful, popular BBQ"),
        FoodInfo("비빔밥 (Bibimbap)", "Mixed rice bowl", "Rice with vegetables and egg", "Colorful, healthy, customizable"),
        FoodInfo("떡볶이 (Tteokbokki)", "Spicy rice cakes", "Street food favorite", "Chewy, spicy, addictive"),
        FoodInfo("삼겹살 (Samgyeopsal)", "Pork belly", "Korean BBQ staple", "Grilled, wrapped in lettuce"),
        FoodInfo("치킨 (Chicken)", "Korean fried chicken", "Crispy, double-fried", "Very popular, many flavors")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(popularFoods) { food ->
            FoodCard(food = food, icon = Icons.Filled.ThumbUp, color = PremiumEmerald)
        }
    }
}

@Composable
fun FamousFoodsContent() {
    val famousFoods = listOf(
        FoodInfo("김치찌개 (Kimchi Jjigae)", "Kimchi stew", "Comfort food, spicy and hearty", "Served bubbling hot"),
        FoodInfo("된장찌개 (Doenjang Jjigae)", "Soybean paste stew", "Traditional, umami-rich", "Healthy, fermented"),
        FoodInfo("갈비탕 (Galbitang)", "Short rib soup", "Clear beef soup, special occasions", "Rich, nourishing"),
        FoodInfo("해물파전 (Haemul Pajeon)", "Seafood pancake", "Crispy pancake with seafood", "Great with makgeolli"),
        FoodInfo("족발 (Jokbal)", "Pig's feet", "Braised, gelatinous texture", "Popular drinking food"),
        FoodInfo("냉면 (Naengmyeon)", "Cold noodles", "Icy broth, chewy noodles", "Perfect for summer")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(famousFoods) { food ->
            FoodCard(food = food, icon = Icons.Filled.Star, color = PremiumAmber)
        }
    }
}

@Composable
fun RegularFoodsContent() {
    val regularFoods = listOf(
        FoodInfo("밥 (Bap)", "Rice", "Staple food, every meal", "White, brown, or mixed grains"),
        FoodInfo("국 (Guk)", "Soup", "Served with every meal", "Light, clear broths"),
        FoodInfo("반찬 (Banchan)", "Side dishes", "Multiple small dishes", "Variety, balance, sharing"),
        FoodInfo("라면 (Ramen)", "Instant noodles", "Quick meal, comfort food", "Very popular, many brands"),
        FoodInfo("김밥 (Kimbap)", "Seaweed rice rolls", "Portable, picnic food", "Similar to sushi"),
        FoodInfo("만두 (Mandu)", "Dumplings", "Steamed or fried", "Filled with meat/vegetables")
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(regularFoods) { food ->
            FoodCard(food = food, icon = Icons.Filled.LunchDining, color = PremiumIndigo)
        }
    }
}

@Composable
fun LikedFoodsContent() {
    val likedFoods = listOf(
        "Korean BBQ - Social dining experience",
        "Fried Chicken - Crispy and flavorful",
        "Tteokbokki - Addictive street food",
        "Bibimbap - Healthy and colorful",
        "Kimchi - Essential with every meal",
        "Seafood - Fresh and diverse",
        "Soups and Stews - Comforting and warm",
        "Banchan - Variety and sharing culture",
        "Desserts - Bingsu, hotteok, patbingsu",
        "Street Food - Convenient and tasty"
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(likedFoods) { food ->
            LikedFoodCard(food = food)
        }
    }
}

@Composable
fun DislikedFoodsContent() {
    val dislikedFoods = listOf(
        "Extremely Spicy Food - Some find too hot",
        "Fermented Seafood - Strong flavors (홍어, 미더덕)",
        "Certain Offal - Not for everyone",
        "Very Salty Dishes - Some traditional foods",
        "Extreme Textures - Some find certain textures challenging"
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassmorphicCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Note",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Korean cuisine is generally well-loved! These are just foods that some people might find challenging due to strong flavors, spiciness, or unique textures. Most Koreans enjoy these foods, but they might be acquired tastes for others.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        items(dislikedFoods) { food ->
            DislikedFoodCard(food = food)
        }
    }
}

data class FoodInfo(
    val korean: String,
    val english: String,
    val description: String,
    val details: String
)

@Composable
fun FoodCard(food: FoodInfo, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    GlassmorphicCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
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
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.korean,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = food.english,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = food.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = food.details,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun LikedFoodCard(food: String) {
    GlassmorphicCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = null,
                tint = PremiumPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = food,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun DislikedFoodCard(food: String) {
    GlassmorphicCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = food,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
                lineHeight = 20.sp
            )
        }
    }
}


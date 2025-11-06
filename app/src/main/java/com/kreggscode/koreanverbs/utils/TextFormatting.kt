package com.kreggscode.koreanverbs.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.koreanverbs.ui.theme.PremiumIndigo
import com.kreggscode.koreanverbs.ui.theme.PremiumPink
import com.kreggscode.koreanverbs.ui.theme.PremiumPurple

/**
 * Formats AI response text by removing markdown symbols and applying proper styling
 */
fun formatAIResponse(text: String): String {
    return text
        .replace("**", "")  // Remove bold markers
        .replace("*", "")   // Remove italic markers
        .replace("##", "")  // Remove heading markers
        .replace("#", "")   // Remove single heading markers
        .replace("```", "") // Remove code block markers
        .trim()
}

/**
 * Composable that renders formatted AI text with styled headings and emphasis
 */
@Composable
fun FormattedAIText(
    text: String,
    modifier: Modifier = Modifier
) {
    val lines = text.split("\n")
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        lines.forEach { line ->
            when {
                // Headings (lines that were marked with ## or #)
                line.trim().length < 50 && line.trim().endsWith(":") -> {
                    Text(
                        text = line.replace(":", "").trim(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumPurple,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                // Bullet points or numbered lists
                line.trim().startsWith("-") || line.trim().matches(Regex("^\\d+\\..*")) -> {
                    val cleanLine = line.trim().removePrefix("-").removePrefix(Regex("^\\d+\\.").toString()).trim()
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            color = PremiumPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = cleanLine,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Regular paragraphs
                line.trim().isNotEmpty() -> {
                    Text(
                        text = buildAnnotatedString {
                            // Find and style Korean text
                            val koreanRegex = Regex("[가-힣]+")
                            var lastIndex = 0
                            
                            koreanRegex.findAll(line).forEach { match ->
                                // Add text before Korean
                                if (match.range.first > lastIndex) {
                                    append(line.substring(lastIndex, match.range.first))
                                }
                                
                                // Add styled Korean text
                                withStyle(
                                    SpanStyle(
                                        color = PremiumIndigo,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append(match.value)
                                }
                                
                                lastIndex = match.range.last + 1
                            }
                            
                            // Add remaining text
                            if (lastIndex < line.length) {
                                append(line.substring(lastIndex))
                            }
                        },
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

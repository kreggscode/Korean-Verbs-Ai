package com.kreggscode.koreanverbs.data.repository

import android.content.Context
import com.kreggscode.koreanverbs.data.models.KoreanVerb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class VerbRepository(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    private var cachedVerbs: List<KoreanVerb>? = null
    private var cachedCategories: List<String>? = null
    
    // Optimized: Load JSON once and cache
    suspend fun getAllVerbs(): List<KoreanVerb> = withContext(Dispatchers.IO) {
        if (cachedVerbs == null) {
            // Use buffered input stream for faster reading
            val inputStream = context.assets.open("korean_verbs.json")
            val jsonString = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            cachedVerbs = json.decodeFromString<List<KoreanVerb>>(jsonString)
        }
        cachedVerbs ?: emptyList()
    }
    
    // Optimized: Cache categories separately for faster access
    suspend fun getCategories(): List<String> = withContext(Dispatchers.IO) {
        if (cachedCategories == null) {
            cachedCategories = getAllVerbs()
                .map { it.category }
                .distinct()
                .filter { it.isNotBlank() && it.lowercase() != "category" }
                .sorted()
        }
        cachedCategories ?: emptyList()
    }
    
    suspend fun getVerbsByCategory(category: String): List<KoreanVerb> = withContext(Dispatchers.IO) {
        getAllVerbs().filter { it.category == category }
    }
    
    suspend fun searchVerbs(query: String): List<KoreanVerb> = withContext(Dispatchers.IO) {
        getAllVerbs().filter {
            it.verb.contains(query, ignoreCase = true) ||
            it.verbRomanization.contains(query, ignoreCase = true) ||
            it.englishMeaning.contains(query, ignoreCase = true) ||
            it.koreanSentence.contains(query, ignoreCase = true) ||
            it.englishSentence.contains(query, ignoreCase = true)
        }
    }
    
    suspend fun getVerbById(id: String): KoreanVerb? = withContext(Dispatchers.IO) {
        getAllVerbs().find { it.id == id }
    }
    
    suspend fun getRandomVerbs(count: Int): List<KoreanVerb> = withContext(Dispatchers.IO) {
        getAllVerbs().shuffled().take(count)
    }
    
    suspend fun getFavoriteVerbs(favoriteIds: Set<String>): List<KoreanVerb> = withContext(Dispatchers.IO) {
        getAllVerbs().filter { it.id in favoriteIds }
    }
}

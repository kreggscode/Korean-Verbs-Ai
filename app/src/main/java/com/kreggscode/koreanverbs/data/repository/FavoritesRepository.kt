package com.kreggscode.koreanverbs.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val _favoritesFlow = MutableStateFlow<Set<String>>(getFavorites())
    val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow
    
    private fun getFavorites(): Set<String> {
        return prefs.getStringSet("favorite_verbs", emptySet()) ?: emptySet()
    }
    
    suspend fun toggleFavorite(verbId: String) = withContext(Dispatchers.IO) {
        val currentFavorites = getFavorites().toMutableSet()
        if (currentFavorites.contains(verbId)) {
            currentFavorites.remove(verbId)
        } else {
            currentFavorites.add(verbId)
        }
        prefs.edit().putStringSet("favorite_verbs", currentFavorites).apply()
        _favoritesFlow.value = currentFavorites
    }
    
    suspend fun isFavorite(verbId: String): Boolean = withContext(Dispatchers.IO) {
        getFavorites().contains(verbId)
    }
    
    suspend fun getFavoriteVerbs(): Set<String> = withContext(Dispatchers.IO) {
        getFavorites()
    }
}

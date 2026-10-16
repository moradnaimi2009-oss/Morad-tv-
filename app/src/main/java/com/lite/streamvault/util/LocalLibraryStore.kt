package com.lite.streamvault.util

import android.content.Context
import org.json.JSONObject

/**
 * Lightweight local "My List" + "Continue Watching" store — no login, no backend.
 * Everything lives on-device only (like the referral device ID), so it resets on
 * uninstall/clear-data, same tradeoff as the rest of the no-login system.
 */
class LocalLibraryStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("morad_library", Context.MODE_PRIVATE)

    // ---- Favorites ("My List") ----
    // key format: "movie:123", "anime:45", "cartoon:9"

    fun isFavorite(key: String): Boolean =
        getFavoriteSet().contains(key)

    fun toggleFavorite(key: String): Boolean {
        val current = getFavoriteSet().toMutableSet()
        val nowFavorite = if (current.contains(key)) {
            current.remove(key); false
        } else {
            current.add(key); true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return nowFavorite
    }

    fun getFavoriteSet(): Set<String> =
        prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    // ---- Continue Watching ----
    // keyed by the video URL itself (always unique per piece of content here)

    fun saveProgress(videoUrl: String, positionMs: Long, durationMs: Long) {
        if (videoUrl.isBlank() || durationMs <= 0) return
        val ratio = positionMs.toFloat() / durationMs
        val all = getAllProgressRaw()
        if (ratio >= 0.9f) {
            // Close enough to the end — count it as watched and clear the resume point.
            markWatched(videoUrl)
            all.remove(videoUrl)
        } else if (ratio in 0.02f..0.9f) {
            all.put(videoUrl, positionMs)
        } else {
            all.remove(videoUrl)
        }
        prefs.edit().putString(KEY_PROGRESS, all.toString()).apply()
    }

    fun getProgress(videoUrl: String): Long {
        val all = getAllProgressRaw()
        return if (all.has(videoUrl)) all.getLong(videoUrl) else 0L
    }

    // ---- Watched episodes ("so you don't lose track / rewatch by mistake") ----

    fun isWatched(videoUrl: String): Boolean =
        prefs.getStringSet(KEY_WATCHED, emptySet())?.contains(videoUrl) == true

    fun markWatched(videoUrl: String) {
        if (videoUrl.isBlank()) return
        val current = (prefs.getStringSet(KEY_WATCHED, emptySet()) ?: emptySet()).toMutableSet()
        current.add(videoUrl)
        prefs.edit().putStringSet(KEY_WATCHED, current).apply()
    }

    private fun getAllProgressRaw(): JSONObject {
        val raw = prefs.getString(KEY_PROGRESS, null) ?: return JSONObject()
        return try { JSONObject(raw) } catch (e: Exception) { JSONObject() }
    }

    companion object {
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_PROGRESS = "progress"
        private const val KEY_WATCHED = "watched"
    }
}

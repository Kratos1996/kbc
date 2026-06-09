package com.ishan.kbc.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(name: String, params: Map<String, Any?> = emptyMap()) {
        analytics.logEvent(name) {
            params.forEach { (k, v) ->
                val key = k.take(40) // Firebase limits param key length
                when (v) {
                    is String -> param(key, v)
                    is Int -> param(key, v.toLong())
                    is Long -> param(key, v)
                    is Double -> param(key, v)
                    is Boolean -> param(key, v.toString())
                    null -> Unit
                    else -> param(key, v.toString())
                }
            }
        }
    }

    fun gameStart(mode: String) = logEvent("game_start", mapOf("mode" to mode))
    fun lifelineUsed(type: String) = logEvent("lifeline_used", mapOf("type" to type))
    fun gameWon(score: Int) = logEvent("game_won", mapOf("score" to score))
    fun gameLost(score: Int) = logEvent("game_lost", mapOf("score" to score))
    fun purchase(productId: String) = logEvent("purchase", mapOf("product_id" to productId))
    fun dailyCompleted(score: Int) = logEvent("daily_completed", mapOf("score" to score))
}

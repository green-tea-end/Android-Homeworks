package com.example.homework_3.data.recent

object RecentRetentionPolicy {
    const val MAX_AGE_DAYS = 30L
    const val MAX_ENTRIES = 200

    val maxAgeMs: Long = MAX_AGE_DAYS * 24L * 60L * 60L * 1000L
}

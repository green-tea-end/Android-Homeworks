package com.example.homework_3.data.settings

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

enum class BackgroundRefreshInterval(val duration: Duration) {
    SIX_HOURS(6.hours),
    TWELVE_HOURS(12.hours),
    TWENTY_FOUR_HOURS(24.hours),
}


package com.example.homework_3.data.settings

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

enum class CacheTtlPreset(val duration: Duration) {
    ONE_HOUR(1.hours),
    SIX_HOURS(6.hours),
    TWENTY_FOUR_HOURS(24.hours),
}


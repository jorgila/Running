package com.estholon.running.domain.model

import android.os.Bundle

data class AnalyticsModel(
    val title: String,
    val analyticsString: List<Pair<String, String>> = emptyList(),
    val analyticsDouble: List<Pair<String, Double>> = emptyList(),
    val analyticsBundle: List<Pair<String, Bundle>> = emptyList(),
    val analyticsLong: List<Pair<String, Long>> = emptyList(),
    val analyticsBundleArray: List<Pair<String, List<Bundle>>> = emptyList()
)
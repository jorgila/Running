package com.estholon.running.domain.repository

import com.estholon.running.domain.model.AnalyticsModel

interface AnalyticsRepository {
    fun sendEvent(analytics: AnalyticsModel)
}
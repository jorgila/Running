package com.estholon.running.data.datasource

import com.estholon.running.data.dto.AnalyticsDto

interface AnalyticsDataSource {
    fun sendEvent(analytics: AnalyticsDto)
}
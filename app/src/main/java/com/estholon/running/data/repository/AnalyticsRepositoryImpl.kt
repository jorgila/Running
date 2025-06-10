package com.estholon.running.data.repository

import com.estholon.running.data.datasource.AnalyticsDataSource
import com.estholon.running.data.mapper.AnalyticsMapper
import com.estholon.running.domain.model.AnalyticsModel
import com.estholon.running.domain.repository.AnalyticsRepository
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDataSource: AnalyticsDataSource,
    private val analyticsMapper: AnalyticsMapper
) : AnalyticsRepository {
    override fun sendEvent(analytics: AnalyticsModel) {
        val analyticsDto = analyticsMapper.analyticsDomainToDto(analytics)
        analyticsDataSource.sendEvent(analyticsDto)
    }
}
package com.estholon.running.data.datasource

import com.estholon.running.data.dto.AnalyticsDto
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

class FirebaseAnalyticsDataSource @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsDataSource {
    override fun sendEvent(analytics: AnalyticsDto) {
        firebaseAnalytics.logEvent(analytics.title){
            analytics.analyticsString.forEach{ param(it.first,it.second)}
            analytics.analyticsLong.forEach{ param(it.first,it.second)}
            analytics.analyticsDouble.forEach{ param(it.first,it.second)}
            analytics.analyticsBundle.forEach{ param(it.first,it.second)}
            analytics.analyticsBundleArray.forEach{ param(it.first,it.second.toTypedArray()) }
        }
    }

}
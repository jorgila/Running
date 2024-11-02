package com.estholon.running.domain.useCase.others

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class GetFormattedStopWatchUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getFormattedStopWatch(ms: Long) : String {
        val date: Date = Date(ms)
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(date)

    }

}
package com.estholon.running.domain.useCase.others

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetFormattedStopWatchUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(ms: Long) : String {
        return formatTime(ms)
    }

    private fun formatTime(ms: Long): String = buildString {
        append("%02d".format(ms / 3600000L))
        append(":")
        append("%02d".format((ms % 3600000L) / 60000))
        append(":")
        append("%02d".format((ms % 60000) / 1000))
    }

}
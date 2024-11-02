package com.estholon.running.domain.useCase.others

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetSecondsFromWatchUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getSecondsFromWatch(watch: String) : Int {

        var seconds = 0
        var w: String = watch
        if(watch.length == 5) w="00:" + w

        seconds += w.subSequence(0,2).toString().toInt() * 3600
        seconds += w.subSequence(3,5).toString().toInt() * 60
        seconds += w.subSequence(6,8).toString().toInt()

        return seconds
    }

}
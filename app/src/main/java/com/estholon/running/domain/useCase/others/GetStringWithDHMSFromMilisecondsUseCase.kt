package com.estholon.running.domain.useCase.others

import javax.inject.Inject

class GetStringWithDHMSFromMilisecondsUseCase @Inject constructor(

) {

    operator fun invoke(miliseconds: Double) : String {
        val totalSeconds = miliseconds / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60

        val d = Math.floor(totalHours / 24).toInt()
        val h = Math.floor(totalHours % 24).toInt()
        val m = Math.floor(totalMinutes % 60).toInt()
        val s = Math.floor(totalSeconds % 60).toInt()

        return "$d d $h h $m m $s s"
    }


}
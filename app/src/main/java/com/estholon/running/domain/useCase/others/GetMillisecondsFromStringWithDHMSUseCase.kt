package com.estholon.running.domain.useCase.others

import javax.inject.Inject
class GetMillisecondsFromStringWithDHMSUseCase @Inject constructor(

){

    operator fun invoke(string: String): Long {

        val parts = string.trim().split(" ")
        var totalMilliseconds : Long = 0

        for (i in parts.indices step 2) {

            if( i + 1 >= parts.size) break

            val value = parts[i].toLongOrNull() ?: continue
            val unit = parts[i+1].lowercase()

            totalMilliseconds += when(unit) {
                "d" -> value * 24 * 60 * 60 * 1000
                "h" -> value * 60 * 60 * 1000
                "m" -> value * 60 * 1000
                "s" -> value * 1000
                else -> 0
            }

        }

        return totalMilliseconds
    }

}
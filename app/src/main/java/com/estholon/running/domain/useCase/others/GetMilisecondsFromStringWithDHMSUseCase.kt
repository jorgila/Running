package com.estholon.running.domain.useCase.others

import javax.inject.Inject
class GetMilisecondsFromStringWithDHMSUseCase @Inject constructor(

){

    operator fun invoke(string: String): Long {

        val parts = string.split(" ")
        var totalMiliseconds : Long = 0

        for (i in parts.indices step 2) {
            val valor = parts[i].toLongOrNull() ?: 0
            val unit = parts[i+1]

            when (unit) {
                "d" -> totalMiliseconds += valor * 24 * 60 * 60 * 1000
                "h" -> totalMiliseconds += valor * 60 * 60 * 1000
                "m" -> totalMiliseconds += valor * 60 * 1000
                "s" -> totalMiliseconds += valor * 1000
            }
        }

        return totalMiliseconds
    }

}
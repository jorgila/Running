package com.estholon.running.domain.useCase.others

import javax.inject.Inject
class GetMilisecondsFromStringWithDHMS @Inject constructor(

){

    operator fun invoke(string: String): Long {
        val regex = Regex("\\$(\\d+)\\s*([dhms])")
        val matches = regex.findAll(string)

        var d = 0L
        var h = 0L
        var m = 0L
        var s = 0L

        for (match in matches) {

            val valor = match.groupValues.getOrNull(1)?.toLongOrNull() ?: 0L
            val unidad = match.groupValues.getOrNull(2)

            when (unidad) {
                "d" -> d = valor
                "h" -> h = valor
                "m" -> m = valor
                "s" -> s = valor
            }
        }

        val milisecondsPerDay = 24 * 60 * 60 * 1000L
        val milisecondsPerHour = 60 * 60 * 1000L
        val milisecondsPerMinute = 60 * 1000L
        val milisecondsPerSecond = 1000L

        return (d * milisecondsPerDay) +
                (h * milisecondsPerHour) +
                (m * milisecondsPerMinute) +
                (s * milisecondsPerSecond)
    }

}
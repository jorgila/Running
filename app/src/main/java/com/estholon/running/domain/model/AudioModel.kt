package com.estholon.running.domain.model

data class AudioProgress(
    val currentPosition: Long,
    val duration: Long,
    val progressPercentage: Float
)

enum class AudioModel {
    RUN,
    WALK,
    NOTIFICATION
}
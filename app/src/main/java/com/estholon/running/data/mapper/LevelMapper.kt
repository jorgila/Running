package com.estholon.running.data.mapper

import com.estholon.running.data.dto.LevelDto
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.domain.model.LevelModel

class LevelMapper {

    fun levelResponseToDto(response: LevelResponse): LevelDto? {
        return if (isValidLevelResponse(response)) {
            LevelDto(
                distanceTarget = response.distanceTarget!!,
                level = response.level!!,
                runsTarget = response.runsTarget!!
            )
        } else null
    }

    fun levelDtoToDomain(dto: LevelDto): LevelModel {
        return LevelModel(
            distanceTarget = dto.distanceTarget,
            level = dto.level,
            runsTarget = dto.runsTarget
        )
    }

    private fun isValidLevelResponse(response: LevelResponse): Boolean {
        return response.distanceTarget != null &&
                response.level != null &&
                response.runsTarget != null
    }

}
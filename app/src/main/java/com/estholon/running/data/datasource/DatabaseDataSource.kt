package com.estholon.running.data.datasource

import com.estholon.running.data.dto.LevelDto
import com.estholon.running.data.dto.LocationDto
import com.estholon.running.data.dto.RunDto
import com.estholon.running.data.dto.TotalDto
import kotlinx.coroutines.flow.Flow

interface DatabaseDataSource {

    fun getTotals() : Flow<TotalDto>
    fun getLevels() : Flow<List<LevelDto>>
    fun getRun(runId: String) : Flow<RunDto>
    fun getAllRuns() : Flow<List<RunDto>>
    suspend fun setTotals(total: TotalDto): Result<Unit>
    suspend fun setRun(run: RunDto): Result<Unit>
    suspend fun deleteRun(runId: String): Result<Unit>
    suspend fun getDistanceRecord() : Result<Double>
    suspend fun getAvgSpeedRecord() : Result<Double>
    suspend fun getSpeedRecord() : Result<Double>
    suspend fun setLocation(runId: String, docName: String, location: LocationDto): Result<Unit>
    suspend fun deleteLocations(runId: String) : Result<Unit>

}
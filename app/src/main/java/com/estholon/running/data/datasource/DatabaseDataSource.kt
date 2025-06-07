package com.estholon.running.data.datasource

import com.estholon.running.data.dto.LevelDTO
import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import kotlinx.coroutines.flow.Flow

interface DatabaseDataSource {

    fun getTotals() : Flow<TotalDTO>
    fun getLevels() : Flow<List<LevelDTO>>
    fun getRun(runId: String) : Flow<RunDTO>
    fun getAllRuns() : Flow<List<RunDTO>>
    suspend fun setTotals(total: TotalDTO): Result<Unit>
    suspend fun setRun(runId:String, run: RunDTO): Result<Unit>
    suspend fun deleteRun(runId: String): Result<Unit>
    suspend fun getDistanceRecord() : Result<Double>
    suspend fun getAvgSpeedRecord() : Result<Double>
    suspend fun getSpeedRecord() : Result<Double>
    suspend fun setLocation(runId: String, docName: String, location: LocationDTO): Result<Unit>
    suspend fun deleteLocations(runId: String) : Result<Unit>

}
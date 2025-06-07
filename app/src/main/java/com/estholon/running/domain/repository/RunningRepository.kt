package com.estholon.running.domain.repository

import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.model.TotalModel
import kotlinx.coroutines.flow.Flow

interface RunningRepository {

    fun getTotals() : Flow<TotalModel>
    fun getLevels() : Flow<List<LevelModel>>
    fun getRun(id: String) : Flow<RunModel>
    fun getAllRuns() : Flow<List<RunModel>>
    suspend fun setTotals(total: TotalModel) : Result<Unit>
    suspend fun setRun(run: RunModel) : Result<Unit>
    suspend fun deleteRun(id: String) : Result<Unit>
    suspend fun getDistanceRecord() : Result<Double>
    suspend fun getAvgSpeedRecord() : Result<Double>
    suspend fun getSpeedRecord() : Result<Double>
    suspend fun setLocation(runId: String, docName: String, location: LocationModel) : Result<Unit>
    suspend fun deleteLocations(runId: String) : Result<Unit>

}
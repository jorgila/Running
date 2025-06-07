package com.estholon.running.data.repository

import com.estholon.running.data.datasource.DatabaseDataSource
import com.estholon.running.data.mapper.LevelMapper
import com.estholon.running.data.mapper.LocationMapper
import com.estholon.running.data.mapper.RunMapper
import com.estholon.running.data.mapper.TotalMapper
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.LocationModel
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.model.TotalModel
import com.estholon.running.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RunningRepositoryImpl @Inject constructor(
    private val databaseDataSource: DatabaseDataSource,
    private val runMapper: RunMapper,
    private val totalMapper: TotalMapper,
    private val levelMapper: LevelMapper,
    private val locationMapper: LocationMapper
) : RunningRepository {

    override fun getTotals(): Flow<TotalModel> {
        return databaseDataSource.getTotals()
            .map { dto -> totalMapper.totalDtoToDomain(dto) }
    }

    override fun getLevels(): Flow<List<LevelModel>> {
        return databaseDataSource.getLevels()
            .map { dtos -> dtos.mapNotNull { levelMapper.levelDtoToDomain(it) } }
    }

    override fun getRun(id: String): Flow<RunModel> {
        return databaseDataSource.getRun(id)
            .map { dto -> runMapper.runDtoToDomain(dto) }
    }

    override fun getAllRuns(): Flow<List<RunModel>> {
        return databaseDataSource.getAllRuns()
            .map { dtos -> dtos.mapNotNull { runMapper.runDtoToDomain(it) } }
    }

    override suspend fun setTotals(total: TotalModel): Result<Unit> {
            val dto = totalMapper.totalDomainToDto(total)
            return databaseDataSource.setTotals(dto)
    }

    override suspend fun setRun(run: RunModel): Result<Unit> {
            val dto = runMapper.runDomainToDto(run)
            return databaseDataSource.setRun(dto)
    }

    override suspend fun deleteRun(id: String): Result<Unit> {
        return databaseDataSource.deleteRun(id)
    }

    override suspend fun getDistanceRecord(): Result<Double> {
        return databaseDataSource.getDistanceRecord()
    }

    override suspend fun getAvgSpeedRecord(): Result<Double> {
        return databaseDataSource.getAvgSpeedRecord()
    }

    override suspend fun getSpeedRecord(): Result<Double> {
        return databaseDataSource.getSpeedRecord()
    }

    override suspend fun setLocation(
        runId: String,
        docName: String,
        location: LocationModel
    ): Result<Unit> {
            val dto = locationMapper.locationDomainToDto(location)
            return databaseDataSource.setLocation(runId,docName,dto)
    }

    override suspend fun deleteLocations(runId: String): Result<Unit> {
        return databaseDataSource.deleteLocations(runId)
    }

}
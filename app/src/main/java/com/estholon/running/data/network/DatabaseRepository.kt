package com.estholon.running.data.network

import android.util.Log
import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.TotalModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.logging.Level
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    val db: FirebaseFirestore,
    val authManager: AuthManager
) {

    companion object {
        const val COLLECTION_LEVELS_RUNNING = "levelsRunning"
        const val COLLECTION_RUNS_RUNNING = "runsRunning"
        const val COLLECTION_TOTALS_RUNNING = "totalsRunning"
    }

    fun getTotals() : Flow<TotalModel> {

        return db
            .collection(COLLECTION_TOTALS_RUNNING)
            .document(authManager.getCurrentEmail().toString())
            .snapshots()
            .mapNotNull { qr ->
                qr.toObject(TotalResponse::class.java)?.let { tr ->
                    totalToDomain(tr)
                }
            }

    }

    fun getLevels() : Flow<List<LevelModel>> {

        return db
            .collection(COLLECTION_LEVELS_RUNNING)
            .snapshots()
            .mapNotNull { qr ->
                val levelsList = mutableListOf<LevelModel>()
                for (document in qr.documents) {
                    document.toObject(LevelResponse::class.java)?.let { lr ->
                        levelToDomain(lr)?.let { levelModel ->
                            levelsList.add(levelModel)
                        }
                    }
                }
                levelsList.sortedBy { it.distanceTarget }
            }


    }

    private fun totalToDomain(tr: TotalResponse): TotalModel? {
        return if (
            tr.recordAvgSpeed != null &&
            tr.recordDistance != null &&
            tr.recordSpeed != null &&
            tr.totalDistance != null &&
            tr.totalRuns != null &&
            tr.totalTime != null
        ) {

            TotalModel(
                recordAvgSpeed = tr.recordAvgSpeed,
                recordDistance = tr.recordDistance,
                recordSpeed = tr.recordSpeed,
                totalDistance = tr.totalDistance,
                totalRuns = tr.totalRuns,
                totalTime = tr.totalTime
            )

        } else {
            null
        }
    }

    private fun levelToDomain(lr: LevelResponse): LevelModel? {
        return if (
            lr.distanceTarget != null &&
            lr.level !=null &&
            lr.runsTarget !=null
        ) {

            LevelModel(
                distanceTarget = lr.distanceTarget,
                level = lr.level,
                runsTarget = lr.runsTarget
            )

        } else {
            null
        }
    }

    fun setTotals(dto: TotalDTO) {
        val model = hashMapOf(
            "recordAvgSpeed" to dto.recordAvgSpeed,
            "recordDistance" to dto.recordDistance,
            "recordSpeed" to dto.recordSpeed,
            "totalDistance" to dto.totalDistance,
            "totalRuns" to dto.totalRuns,
            "totalTime" to dto.totalTime
        )
        db.collection(COLLECTION_TOTALS_RUNNING).document(authManager.getCurrentEmail().toString()).set(model)

    }

    fun setRun(id: String, dto: RunDTO){

        val runId = "${authManager.getCurrentEmail()}${id}"

        val model = hashMapOf(
            "user" to dto.user,
            "startDate" to dto.startDate,
            "startTime" to dto.startTime,
            "kpiDuration" to dto.kpiDuration,
            "kpiDistance" to dto.kpiDistance,
            "kpiAvgSpeed" to dto.kpiAvgSpeed,
            "kpiMaxSpeed" to dto.kpiMaxSpeed,
            "kpiMinAltitude" to dto.kpiMinAltitude,
            "kpiMaxAltitude" to dto.kpiMaxAltitude,
            "goalDurationSelected" to dto.goalDurationSelected,
            "goalHoursDefault" to dto.goalHoursDefault,
            "goalMinutesDefault" to dto.goalMinutesDefault,
            "goalSecondsDefault" to dto.goalSecondsDefault,
            "goalDistanceDefault" to dto.goalDistanceDefault,
            "goalDistance" to dto.goalDistance,
            "intervalDefault" to dto.intervalDefault,
            "intervalRunDuration" to dto.intervalRunDuration,
            "intervalWalkDuration" to dto.intervalWalkDuration,
            "rounds" to dto.rounds
        )
        db
            .collection(COLLECTION_RUNS_RUNNING)
            .document(runId)
            .set(model)
    }

    fun setLocation(id: String, docName: String,dto: LocationDTO){

        val user = authManager.getCurrentEmail()

        val model = hashMapOf(
            "time" to dto.time,
            "latitude" to dto.latitude,
            "longitude" to dto.longitude,
            "altitude" to dto.altitude,
            "hasAltitude" to dto.hasAltitude,
            "speedFromGoogle" to dto.speedFromGoogle,
            "speedFromApp" to dto.speedFromApp,
            "isMaxSpeed" to dto.isMaxSpeed,
            "isRunInterval" to dto.isRunInterval
        )

        db
            .collection("locations/$user/$id")
            .document(docName)
            .set(model)
    }

}
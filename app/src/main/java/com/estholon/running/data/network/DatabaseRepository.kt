package com.estholon.running.data.network

import android.util.Log
import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.data.network.response.RunResponse
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.RunModel
import com.estholon.running.domain.model.TotalModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
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

    fun getRun(
        id: String
    ) : Flow<RunModel> {

        val runId : String = "${authManager.getCurrentEmail()}$id"

        return db
            .collection(COLLECTION_RUNS_RUNNING)
            .document(runId)
            .snapshots()
            .mapNotNull { qr ->
                qr.toObject(RunResponse::class.java)?.let { rr ->
                    runToDomain(rr)
                }
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

    private fun runToDomain(rr: RunResponse) : RunModel? {
        return if (
            rr.user!=null &&
            rr.runId!=null &&
            rr.startDate != null &&
            rr.startTime != null &&
            rr.kpiDuration != "00:00:00"
        ) {
            RunModel(
                user = rr.user,
                runId = rr.runId,
                startDate = rr.startDate,
                startTime = rr.startTime,
                kpiDuration = rr.kpiDuration,
                kpiDistance = rr.kpiDistance,
                kpiAvgSpeed = rr.kpiAvgSpeed,
                kpiMaxSpeed = rr.kpiMaxSpeed,
                kpiMinAltitude = rr.kpiMinAltitude,
                kpiMaxAltitude = rr.kpiMaxAltitude,
                goalDurationSelected = rr.goalDurationSelected,
                goalHoursDefault = rr.goalHoursDefault,
                goalMinutesDefault = rr.goalMinutesDefault,
                goalSecondsDefault = rr.goalSecondsDefault,
                goalDistanceDefault = rr.goalDistanceDefault,
                intervalDefault = rr.intervalDefault,
                intervalRunDuration = rr.intervalRunDuration,
                intervalWalkDuration = rr.intervalWalkDuration,
                rounds = rr.rounds
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

    fun setRun(id: String?, dto: RunDTO){

        if(id != null){

            val runId = "${authManager.getCurrentEmail()}${id}"

            val model = hashMapOf(
                "user" to dto.user,
                "runId" to dto.runId,
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

    }

    fun setLocation(id: String?, docName: String,dto: LocationDTO){

        if (id!=null){
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

    fun deleteRunAndLinkedData(
        id:String,
        callback: (Boolean) -> Unit
    ) {

        val user = authManager.getCurrentEmail()
        val runId = "$user$id"

        db
            .collection(COLLECTION_RUNS_RUNNING)
            .document(runId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
            }

    }

    fun getDistanceRecord(callback: (Boolean, Double) -> Unit) {

        var record: Double = 0.0

        db
            .collection(COLLECTION_RUNS_RUNNING)
            .orderBy("kpiDistance", Query.Direction.DESCENDING)
            .whereEqualTo("user",authManager.getCurrentEmail())
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() != 0) {
                    record = documents.documents[0].get("kpiDistance").toString().toDouble()
                }
                callback(true, record)
            }
            .addOnFailureListener{
                callback(false, 0.0)
            }

    }

    fun getAvgSpeedRecord(
        callback: (Boolean,Double) -> Unit
    ) {
        var record: Double = 0.0

        db
            .collection(COLLECTION_RUNS_RUNNING)
            .orderBy("kpiAvgSpeed", Query.Direction.DESCENDING)
            .whereEqualTo("user",authManager.getCurrentEmail())
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() != 0) {
                    record = documents.documents[0].get("kpiAvgSpeed").toString().toDouble()
                }
                callback(true, record)
            }
            .addOnFailureListener{
                callback(false, 0.0)
            }

    }

    fun getSpeedRecord(callback: (Boolean, Double) -> Unit) {
        var record: Double = 0.0

        db
            .collection(COLLECTION_RUNS_RUNNING)
            .orderBy("kpiMaxSpeed", Query.Direction.DESCENDING)
            .whereEqualTo("user",authManager.getCurrentEmail())
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() != 0) {
                    record = documents.documents[0].get("kpiMaxSpeed").toString().toDouble()
                }
                callback(true,record)
            }
            .addOnFailureListener{
                callback(false,0.0)
            }
    }

    fun deleteLocations(
        id: String,
        callback: (Boolean) -> Unit
    ){

        db
            .collection("locations/${authManager.getCurrentEmail()}/${id}")
            .get()
            .addOnSuccessListener { documents ->
                var status : Boolean = true
                for (document in documents){
                    db
                        .collection("locations/${authManager.getCurrentEmail()}/${id}")
                        .document(document.id)
                        .delete()
                        .addOnFailureListener{
                            status = false
                        }
                }
                callback(status)
            }
            .addOnFailureListener{
                callback(false)
            }

    }

    fun getAllRuns(): Flow<List<RunModel>> {
        return db
            .collection(COLLECTION_RUNS_RUNNING)
            .snapshots()
            .mapNotNull { qr ->
                val runsList = mutableListOf<RunModel>()
                for (document in qr.documents){
                    document.toObject(RunResponse::class.java)?.let { rr ->
                        runToDomain(rr)?.let { runModel ->
                            runsList.add(runModel)
                        }
                    }
                }
                runsList.sortedBy { it.startDate }
            }

    }

}
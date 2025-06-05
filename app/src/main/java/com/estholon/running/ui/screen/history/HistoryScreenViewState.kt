package com.estholon.running.ui.screen.history

import com.estholon.running.domain.model.RunModel

class HistoryScreenViewState {

    data class HistoryUIState(
        val message: Boolean = false,
        val kpiTotalDistance : Double = 0.00,
        val kpiTotalRuns : Double = 0.00,
        val kpiTotalTime : String = "0 d 0 h 0 m 0 s",
        val runs: List<RunModel> = emptyList()
    )


}
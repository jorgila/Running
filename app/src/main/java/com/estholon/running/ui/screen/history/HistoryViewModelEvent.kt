package com.estholon.running.ui.screen.history

sealed class HistoryViewModelEvent {

    data object OnZoomAll: HistoryViewModelEvent()

}
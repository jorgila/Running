package com.estholon.running.ui.screen.history

sealed class HistoryScreenEvent {
    data object OnZoomAll : HistoryScreenEvent()
}
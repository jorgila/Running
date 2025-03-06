package com.estholon.running.ui.screen.home

sealed class HomeScreenEvent {
    data object OnZoomAll : HomeScreenEvent()
}
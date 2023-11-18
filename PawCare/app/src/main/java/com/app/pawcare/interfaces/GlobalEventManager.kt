package com.app.pawcare.interfaces

object GlobalEventManager {
    var onPetUpdateListener: (() -> Unit)? = null
}
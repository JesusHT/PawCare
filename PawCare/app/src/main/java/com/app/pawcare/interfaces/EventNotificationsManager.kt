package com.app.pawcare.interfaces

object EventNotificationsManager {
    var onNotificationChangedListener: (() -> Unit)? = null
}
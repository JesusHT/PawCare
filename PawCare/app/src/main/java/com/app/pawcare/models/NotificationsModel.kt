package com.app.pawcare.models

data class NotificationsModel(
    val idNotification   : Int,
    val idPet            : Int,
    val title            : String,
    val description      : String,
    val typeNotification : String,
    val date             : String,
    val time             : String
)

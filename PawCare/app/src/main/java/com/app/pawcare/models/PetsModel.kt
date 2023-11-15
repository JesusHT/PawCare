package com.app.pawcare.models

data class PetsModel(
    val idUser: Int,
    val idPet : Int,
    val name  : String,
    val raza  : String,
    val photo : String,
    val peso  : Int,
    val sex   : String,
    val birthday : String,
    val typePet : String
)
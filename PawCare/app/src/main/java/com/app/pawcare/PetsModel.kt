package com.app.pawcare

data class PetsModel(
    val idPet : Int,
    val name  : String,
    val raza  : String,
    val photo : String,
    val peso  : Int,
    val sex   : String,
    val birthday : String
)
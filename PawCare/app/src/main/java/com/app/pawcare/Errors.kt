package com.app.pawcare

class Errors {
    companion object {
        // If you want to add a new error, it is necessary to put "const val ERROR_NAME = "Message"".
        const val ERROR_CHARACTERS  = "Caracteres no validos"
        const val ERROR_VALIDATE    = "Credenciales invalidas"
        const val ERROR_PASSWORD    = "Contresaña incorrecta"
        const val ERROR_EMAIL       = "Correo invalido"
        const val ERROR_DATA_EMPTY  = "Todos los campos deben estar llenos"
    }
}
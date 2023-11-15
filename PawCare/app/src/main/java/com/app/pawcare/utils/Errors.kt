package com.app.pawcare.utils

class Errors {
    companion object {
        // If you want to add a new error, it is necessary to put "const val ERROR_NAME = "Message"".

        // FIELDS
        const val ERROR_VALIDATE           = "Credenciales invalidas"
        const val ERROR_INVALID_NAME       = "El nombre contiene caracteres no permitidos. Utiliza solo letras y números."
        const val ERROR_INVALID_PASSWORD   = "La contraseña debe tener al menos 8 caracteres y contener al menos una mayúscula, un número y un carácter especial (!@#\$%^&*)."
        const val ERROR_INVALID_EMAIL      = "La dirección de correo electrónico no es válida o contiene caracteres no permitidos."
        const val ERROR_DATA_EMPTY         = "Todos los campos deben estar llenos"
        const val ERROR_EMAIL              = "Correo invalido"
        const val ERROR_EMAIL_EXIST        = "El correo ya esta registrado"
        const val ERROR_EMAIL_NO_EXIST     = "El correo no esta asociado a una cuenta."
        const val ERROR_PASSWORD_MISMATCH  = "La confirmación de contraseña no coincide con la contraseña."
        const val ERROR_TERMS_NOT_ACCEPTED = "Debes aceptar los términos y condiciones para continuar."
        const val ERROR_CAPTCHA            = "Código incorrecto. Intenta de nuevo."
        const val ERROR_CREATE_ACCOUNT     = "Error al crear la cuenta. Intenta de nuevo."
        const val ERROR_PASSWORD_VALIDATE  = "La contraseña no coincide."
        const val ERROR_PASSWORD_SAME      = "La nueva contraseña no debe ser igual que la anterior."

        const val ERROR_UPDATE_PASSWORD    = "Hubo un problema al actualizar la contraseña. Intente de nuevo"
        const val ERROR_INVALID_WEIGHT     = "Valor no valido, el peso debe ser mayor a 0."
        const val ERROR_SELECT_SEX         = "Debe elegir el sexo de la mascota"


        //DB ERRORS
        const val ERROR_DB_PETS = "Ah ocurrido un error. Intentalo de nuevo."
    }
}
package com.app.pawcare.utils

class Errors {
    companion object {
        // If you want to add a new error, it is necessary to put "const val ERROR_NAME = "Message"".

        // FIELDS
        const val ERROR_VALIDATE               = "Credenciales inválidas."
        const val ERROR_INVALID_NAME           = "El nombre contiene caracteres no permitidos. Utiliza solo letras y números."
        const val ERROR_INVALID_PASSWORD       = "La contraseña debe tener al menos 8 caracteres y contener al menos una mayúscula, un número y un carácter especial (!@#\$%^&*)."
        const val ERROR_INVALID_EMAIL          = "La dirección de correo electrónico no es válida o contiene caracteres no permitidos."
        const val ERROR_DATA_EMPTY             = "Todos los campos deben estar llenos."
        const val ERROR_EMAIL                  = "Correo inválido."
        const val ERROR_EMAIL_EXIST            = "El correo ya está registrado."
        const val ERROR_EMAIL_NO_EXIST         = "El correo no está asociado a una cuenta."
        const val ERROR_PASSWORD_MISMATCH      = "La confirmación de contraseña no coincide con la contraseña."
        const val ERROR_TERMS_NOT_ACCEPTED     = "Debes aceptar los términos y condiciones para continuar."
        const val ERROR_CAPTCHA                = "Código incorrecto. Inténtalo de nuevo."
        const val ERROR_CREATE_ACCOUNT         = "Error al crear la cuenta. Inténtalo de nuevo."
        const val ERROR_PASSWORD_VALIDATE      = "La contraseña no coincide."
        const val ERROR_PASSWORD_SAME          = "La nueva contraseña no debe ser igual a la anterior."
        const val ERROR_UPDATE_PASSWORD        = "Hubo un problema al actualizar la contraseña. Inténtalo de nuevo."
        const val ERROR_INVALID_WEIGHT         = "Valor no válido. El peso debe ser mayor a 0 y menor que 100."
        const val ERROR_SELECT_SEX             = "Debes elegir el sexo de la mascota."
        const val ERROR_BIRTHDAY               = "La fecha es inválida."
        const val ERROR_SELECT_OPTION_REMINDER = "Debes elegir el tipo de recordatorio."
        const val ERROR_SELECT_OPTION_PET      = "Debes elegir una mascota."
        const val ERROR_DATE                   = "La fecha no puede ser días anteriores."
        const val ERROR_PETS                   = "No hay mascotas guardadas."

        // DB ERRORS
        const val ERROR_DB_PETS          = "Ha ocurrido un error. Inténtalo de nuevo."
        const val ERROR_DB_NOTIFICATIONS = "Ha ocurrido un error. Inténtalo de nuevo."

    }
}
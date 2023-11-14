package com.app.pawcare.config

class Config {

    // If you want to add a new url, you need to put "$URL_MAIN/controller/" or "$URL_MAIN/controller/function"
    companion object {
        private const val URL_MAIN  = "https://pawcare.mx"

        // USER
        const val URL_LOGIN           = "$URL_MAIN/users/authenticate"
        const val URL_REGISTER        = "$URL_MAIN/users/save"
        const val URL_VALIDATE_USER   = "$URL_MAIN/users/exist"
        const val URL_UPDATE          = "$URL_MAIN/users/update"
        const val URL_CREATE_PASSWORD = "$URL_MAIN/users/create"
        const val URL_VALIDATE        = "$URL_MAIN/users/validate"

        const val URL_TERMS         = "$URL_MAIN/terminosycondiciones"
        const val URL_ABOUT         = "$URL_MAIN/acercade"
        const val URL_TIPS          = "$URL_MAIN/tips/gettips"
        const val URL_IMG           = "$URL_MAIN/public/img/"
        const val URL_CAPTCHA       = "$URL_MAIN/captcha/getcode"
    }
}
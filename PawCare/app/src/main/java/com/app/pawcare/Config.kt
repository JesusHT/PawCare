package com.app.pawcare

class Config {

    // If you want to add a new url, you need to put "$URL_MAIN/controller/" or "$URL_MAIN/controller/function"
    companion object {
        const val URL_MAIN          = "http://192.168.1.69"
        const val URL_LOGIN         = "$URL_MAIN/login/authenticate"
        const val URL_USER          = "$URL_MAIN/login/getuser"
        const val URL_TERMS         = "$URL_MAIN/terminosycondiciones"
        const val URL_TIPS          = "$URL_MAIN/tips/gettips"
        const val URL_IMG           = "$URL_MAIN/public/img/"
        const val URL_CAPTCHA       = "$URL_MAIN/captcha/getcode"
        const val URL_REGISTER      = "$URL_MAIN/register/setnewuser"
        const val URL_VALIDATE_USER = "$URL_MAIN/register/validateuser"
    }
}
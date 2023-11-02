package com.app.pawcare

import android.view.View
import android.widget.TextView

object MessageUtils {

    private var errorTextView:   TextView? = null
    private var successTextView: TextView? = null
    private var warningTextView: TextView? = null
    private var infoTextView:    TextView? = null

    fun setErrorView(textView: TextView) {
        errorTextView = textView
    }

    fun setSuccessView(textView: TextView) {
        successTextView = textView
    }

    fun setWarningView(textView: TextView) {
        warningTextView = textView
    }

    fun setInfoView(textView: TextView){
        infoTextView = textView
    }

    fun showError(message: String) {
        clearMessages()
        errorTextView?.text = message
        errorTextView?.visibility = View.VISIBLE
    }

    fun showSuccess(message: String) {
        clearMessages()
        successTextView?.text = message
        successTextView?.visibility = View.VISIBLE
    }

    fun clearMessages() {
        errorTextView?.text = ""
        errorTextView?.visibility = View.GONE

        successTextView?.text = ""
        successTextView?.visibility = View.GONE
    }

    fun clearTextMessages(){
        errorTextView?.text = ""
        successTextView?.text = ""
    }
}

package com.app.pawcare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.pawcare.databinding.TemplateSuccessBinding

class TemplateSuccess : AppCompatActivity() {
    lateinit var b : TemplateSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = TemplateSuccessBinding.inflate(layoutInflater)
        setContentView(b.root)

        val typeAction = intent.getStringExtra("typeAction")

        if (typeAction != null) {
            selectTypeView(typeAction)
        }

        b.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun selectTypeView(type: String){
        if (type == "Register"){
            b.title.text     = "¡Bienvenido a PawCare!"
            b.subtitle.text  = "Cuenta creada exitosamente"
        } else {
            b.title.text     = "Contraseña restablecida correctamente"
            b.subtitle.text  = "Su nueva contraseña fue en viada a su correo"
        }
    }
}
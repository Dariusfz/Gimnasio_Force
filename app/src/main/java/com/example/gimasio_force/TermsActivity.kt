package com.example.gimasio_force

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    private lateinit var btnAceptar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms)

        btnAceptar = findViewById(R.id.btn_aceptar)

        btnAceptar.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }


    }
}
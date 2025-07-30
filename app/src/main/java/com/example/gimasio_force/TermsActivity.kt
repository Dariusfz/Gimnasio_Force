package com.example.gimasio_force

import android.os.Bundle
import android.view.View
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    private lateinit var btnAceptar: Button


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms)

        btnAceptar= findViewById(R.id.btn_aceptar)



    }
}
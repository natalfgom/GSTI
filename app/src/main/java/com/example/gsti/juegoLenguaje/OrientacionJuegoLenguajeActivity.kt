package com.example.gsti.juegoLenguaje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.juegoLenguaje.MecanicaLenguaje

import com.example.gsti.R



class OrientacionJuegoLenguajeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orientacion_lenguaje)

        val nextButton: Button = findViewById(R.id.nextButton)

        nextButton.setOnClickListener {
            val intent = Intent(this, MecanicaLenguaje::class.java)
            startActivity(intent)
            finish()
        }

    }
}

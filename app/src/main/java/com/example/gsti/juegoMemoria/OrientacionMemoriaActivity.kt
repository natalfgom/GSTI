package com.example.gsti.juegoMemoria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class OrientacionMemoriaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orientacion_memoria)

        val nextButton: Button = findViewById(R.id.nextButton)

        // Navegar a la pantalla de Comprensión
        nextButton.setOnClickListener {
            val intent = Intent(this, MecanicaMemoria::class.java)
            startActivity(intent)
            finish() // Opcional: Cierra la pantalla actual
        }
    }
}
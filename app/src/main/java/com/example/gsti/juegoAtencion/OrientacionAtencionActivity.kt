package com.example.gsti.juegoAtencion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.juegoMemoria.MecanicaMemoria

class OrientacionAtencionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orientacion_atencion)

        val nextButton: Button = findViewById(R.id.nextButton)

        // Navegar a la pantalla de Comprensión
        nextButton.setOnClickListener {
            val intent = Intent(this, ComprensionAtencionActivity::class.java)
            startActivity(intent)
            finish() // Opcional: Cierra la pantalla actual
        }
    }
}
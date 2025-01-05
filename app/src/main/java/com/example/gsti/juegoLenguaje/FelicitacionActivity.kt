package com.example.gsti.juegoLenguaje

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class FelicitacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_lenguaje)

        // Recoger los aciertos del Intent
        val aciertos = intent.getIntExtra("ACERTADOS", 0)

        // Actualizar el TextView con los aciertos
        val textViewResultado = findViewById<TextView>(R.id.textViewResultadoSuccess)
        textViewResultado.text = getString(R.string.resultado_aciertos, aciertos)

        // Botón para salir
        val buttonSalir = findViewById<Button>(R.id.buttonSalirSuccess)
        buttonSalir.setOnClickListener {
            finish() // Finalizar esta actividad y volver al inicio
        }
    }
}

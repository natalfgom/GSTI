package com.example.gsti.juegoLenguaje

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.juegoLenguaje.OrientacionJuegoLenguajeActivity

class IntentarDeNuevoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failure_lenguaje)

        // Recoger los aciertos del Intent
        val aciertos = intent.getIntExtra("ACERTADOS", 0)

        // Actualizar el TextView con los aciertos
        val textViewResultado = findViewById<TextView>(R.id.textViewResultadoFailure)
        textViewResultado.text = getString(R.string.resultado_aciertos, aciertos)

        // Botón para intentar de nuevo
        val buttonIntentarDeNuevo = findViewById<Button>(R.id.buttonIntentarDeNuevoFailure)
        buttonIntentarDeNuevo.setOnClickListener {
            // Navegar a la pantalla de orientación
            val intent = Intent(this, OrientacionJuegoLenguajeActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la pantalla actual para evitar volver atrás
        }

    }
}

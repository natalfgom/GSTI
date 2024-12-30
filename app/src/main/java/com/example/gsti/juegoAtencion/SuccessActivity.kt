package com.example.gsti.juegoAtencion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.menuInicio.InicioPaciente

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val stars = intent.getIntExtra("stars", 0) // Obtener el número de estrellas
        val successMessage: TextView = findViewById(R.id.successMessage)
        val star1: ImageView = findViewById(R.id.star1)
        val star2: ImageView = findViewById(R.id.star2)
        val star3: ImageView = findViewById(R.id.star3)
        val goToMenuButton: Button = findViewById(R.id.goToMenuButton)

        // Actualizar el mensaje de éxito
        successMessage.text = "¡Felicidades! Has obtenido $stars estrellas."

        // Mostrar las estrellas según el desempeño (de izquierda a derecha, invertido)
        if (stars >= 1) star1.setImageResource(R.drawable.ic_star_empty) else star1.setImageResource(R.drawable.ic_star_filled)
        if (stars >= 2) star2.setImageResource(R.drawable.ic_star_empty) else star2.setImageResource(R.drawable.ic_star_filled)
        if (stars >= 3) star3.setImageResource(R.drawable.ic_star_empty) else star3.setImageResource(R.drawable.ic_star_filled)

        // Configurar el botón para volver al menú principal
        goToMenuButton.setOnClickListener {
            val intent = Intent(this, InicioPaciente::class.java) // Reemplaza con tu actividad principal
            startActivity(intent)
            finish()
        }
    }
}

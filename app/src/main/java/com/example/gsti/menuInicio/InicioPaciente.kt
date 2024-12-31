package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.JuegosActivity
import com.example.gsti.OrientacionActivity
import com.example.gsti.R
import com.example.gsti.juegoAtencion.MecanicaAtencion


class InicioPaciente : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_paciente)

        // Referencias a los botones usando sus IDs
        val btnJuegos = findViewById<LinearLayout>(R.id.btnJuegos)
        val btnEstadisticas = findViewById<LinearLayout>(R.id.btnEstadisticas)
        val btnNotificaciones = findViewById<LinearLayout>(R.id.btnNotificaciones)

        // Acción para "Juegos"
        btnJuegos.setOnClickListener {
            // Navegar a la pantalla de orientación antes del juego
            val intent = Intent(this, OrientacionActivity::class.java)
            startActivity(intent)
        }


        // Acción para "Estadísticas"
        btnEstadisticas.setOnClickListener {
            // TODO: Implementar navegación a EstadisticasActivity
            Toast.makeText(this, "Ir a Estadísticas", Toast.LENGTH_SHORT).show()
        }

        // Acción para "Notificaciones"
        btnNotificaciones.setOnClickListener {
            // TODO: Implementar navegación a NotificacionesActivity
            Toast.makeText(this, "Ir a Notificaciones", Toast.LENGTH_SHORT).show()
        }
    }
}

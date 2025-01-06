package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.configuracionJuegos.ListaPacientesActivity
import com.example.gsti.juegoLenguaje.OrientacionJuegoLenguajeActivity

class InicioMedico : AppCompatActivity() {

    // Declaramos las variables para los botones
    private lateinit var btnConfiguracionJuegos: LinearLayout
    private lateinit var btnEstadisticas: LinearLayout
    private lateinit var btnNotificaciones: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_medico)

        // Inicializar vistas
        btnConfiguracionJuegos = findViewById(R.id.btnConfiguracionJuegos)
        btnEstadisticas = findViewById(R.id.btnEstadisticas)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)

        // Configurar listeners para los botones
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        // Botón Configuración de Juegos
        btnConfiguracionJuegos.setOnClickListener {
            Toast.makeText(this, "Configuración de Juegos seleccionada", Toast.LENGTH_SHORT).show()
            // Aquí más adelante iniciaremos la pantalla Configuración de Juegos
            val intent = Intent(this, ListaPacientesActivity::class.java)
            startActivity(intent)
        }

        // Botón Estadísticas
        btnEstadisticas.setOnClickListener {
            Toast.makeText(this, "Estadísticas seleccionada", Toast.LENGTH_SHORT).show()
            // Aquí más adelante iniciaremos la pantalla Estadísticas
        }

        // Botón Notificaciones
        btnNotificaciones.setOnClickListener {
            Toast.makeText(this, "Notificaciones seleccionada", Toast.LENGTH_SHORT).show()
            // Aquí más adelante iniciaremos la pantalla Comunicaciones o Notificaciones
        }
    }
}

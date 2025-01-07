package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.Estadisticas.EstadisticasPacienteActivity
import com.example.gsti.R

class InicioFamiliar : AppCompatActivity() {

    private lateinit var btnEstadisticas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_familiar) // Referencia al layout

        // Vincular el botón de estadísticas
        btnEstadisticas = findViewById(R.id.btnEstadisticas)

        // Obtener el paciente asociado del intent
        val pacienteAsociado = intent.getStringExtra("PACIENTE_ASOCIADO")

        // Configurar la acción del botón
        btnEstadisticas.setOnClickListener {
            if (pacienteAsociado != null) {
                // Redirigir a Estadísticas del paciente
                val intent = Intent(this, EstadisticasPacienteActivity::class.java)
                intent.putExtra("EMAIL_PACIENTE", pacienteAsociado)
                startActivity(intent)
            } else {
                // Mostrar un mensaje si no se encontró el paciente asociado
                Toast.makeText(
                    this,
                    "No se pudo encontrar el paciente asociado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

package com.example.gsti.juegosPaciente

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JuegosPacienteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juegos_paciente)

        val juegosLayout: LinearLayout = findViewById(R.id.juegosLayout)

        // Obtener el email del paciente autenticado
        val pacienteId = FirebaseAuth.getInstance().currentUser?.email ?: "paciente@gmail.com"

        // Consultar los juegos del paciente
        val pacienteRef = db.collection("Pacientes").document(pacienteId)

        pacienteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Leer los campos de los juegos
                val juegoAtencionActivo = document.getBoolean("juegoAtencionActivo") ?: false
                val juegoMemoriaActivo = document.getBoolean("juegoMemoriaActivo") ?: false
                val juegoLenguajeActivo = document.getBoolean("juegoLenguajeActivo") ?: false

                // Crear la lista de juegos activos
                val juegos = mutableListOf<Pair<String, Boolean>>()
                if (juegoAtencionActivo) juegos.add("Juego de Atención" to true)
                if (juegoMemoriaActivo) juegos.add("Juego de Memoria" to true)
                if (juegoLenguajeActivo) juegos.add("Juego de Lenguaje" to true)

                // Mostrar los juegos activos
                for ((juego, activo) in juegos) {
                    // Crear un FrameLayout para contener cada juego dentro de un borde
                    val juegoContainer = FrameLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginStart = 16
                            marginEnd = 16
                            topMargin = 16
                            bottomMargin = 16
                        }
                        // Añadir borde y relleno
                        setBackgroundResource(R.drawable.border)
                        setPadding(16, 16, 16, 16)  // Padding para mayor espacio
                    }

                    // Crear el TextView para el nombre del juego
                    val juegoTextView = TextView(this).apply {
                        text = juego
                        setPadding(16, 16, 16, 16)  // Padding para evitar que el texto quede pegado al borde
                        textSize = 18f
                    }

                    // Agregar el TextView al FrameLayout
                    juegoContainer.addView(juegoTextView)

                    // Establecer el comportamiento de clic en el contenedor
                    juegoContainer.setOnClickListener {
                        // Iniciar el juego correspondiente al clic
                        iniciarJuego(juego)
                    }

                    // Añadir el contenedor del juego al layout principal
                    juegosLayout.addView(juegoContainer)
                }
            } else {
                // Si no se encuentran datos del paciente
                val noJuegosText = TextView(this).apply {
                    text = "No se encontraron juegos para este paciente."
                    setPadding(16, 16, 16, 16)
                    textSize = 18f
                }
                juegosLayout.addView(noJuegosText)
            }
        }.addOnFailureListener { exception ->
            // Manejo de errores
            val errorText = TextView(this).apply {
                text = "Error al obtener los juegos."
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }
            juegosLayout.addView(errorText)
        }
    }

    private fun iniciarJuego(juego: String) {
        when (juego) {
            "Juego de Atención" -> {
                // Iniciar el juego de atención
                Toast.makeText(this, "Iniciando Juego de Atención", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.example.gsti.juegoAtencion.OrientacionAtencionActivity::class.java)
                startActivity(intent)
            }
            "Juego de Memoria" -> {
                // Iniciar el juego de memoria
                Toast.makeText(this, "Iniciando Juego de Memoria", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.example.gsti.juegoMemoria.OrientacionMemoriaActivity::class.java)
                startActivity(intent)
            }
            "Juego de Lenguaje" -> {
                // Iniciar el juego de lenguaje
                Toast.makeText(this, "Iniciando Juego de Lenguaje", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.example.gsti.juegoLenguaje.OrientacionJuegoLenguajeActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Juego no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

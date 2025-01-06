package com.example.gsti.juegosPaciente

import android.content.Intent
import android.os.Bundle
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
//        val pacienteRef = db.collection("Medicos")
//            .document(pacienteId) // Asumimos que el email del paciente es único para su documento
//            .collection("Pacientes")
//            .document(pacienteId) // El documento del paciente

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
                    val juegoTextView = TextView(this).apply {
                        text = juego
                        setPadding(16, 16, 16, 16)
                        textSize = 18f
                    }

                    juegoTextView.setOnClickListener {
                        // Iniciar el juego correspondiente al clic
                        iniciarJuego(juego)
                    }

                    juegosLayout.addView(juegoTextView)
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
                // Aquí puedes iniciar la actividad correspondiente al juego de atención
                Toast.makeText(this, "Iniciando Juego de Atención", Toast.LENGTH_SHORT).show()
                // Ejemplo de iniciar la actividad del juego de atención
                val intent = Intent(this,  com.example.gsti.juegoAtencion.MecanicaAtencion::class.java)
                startActivity(intent)
            }
            "Juego de Memoria" -> {
                // Aquí puedes iniciar la actividad correspondiente al juego de memoria
                Toast.makeText(this, "Iniciando Juego de Memoria", Toast.LENGTH_SHORT).show()
                // Ejemplo de iniciar la actividad del juego de memoria
                val intent = Intent(this, com.example.gsti.juegoMemoria.MecanicaMemoria::class.java)
                startActivity(intent)
            }
            "Juego de Lenguaje" -> {
                // Aquí puedes iniciar la actividad correspondiente al juego de lenguaje
                Toast.makeText(this, "Iniciando Juego de Lenguaje", Toast.LENGTH_SHORT).show()
                // Ejemplo de iniciar la actividad del juego de lenguaje
                val intent = Intent(this, com.example.gsti.juegoLenguaje.MecanicaLenguaje::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Juego no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

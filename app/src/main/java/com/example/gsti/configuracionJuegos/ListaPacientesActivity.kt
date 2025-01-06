package com.example.gsti.configuracionJuegos

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.JuegosActivity
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListaPacientesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pacientes)

        // Contenedor para la lista de pacientes
        val listaPacientesLayout: LinearLayout = findViewById(R.id.listaPacientesLayout)

        // Obtener el email del médico autenticado
        val medicoId = FirebaseAuth.getInstance().currentUser?.email ?: "medico@gmail.com"

        // Consultar los pacientes del médico
        val pacientesRef = db.collection("Medicos").document(medicoId).collection("Pacientes")

        pacientesRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                // En caso de que no haya pacientes, mostrar mensaje
                val noPacientesText = TextView(this).apply {
                    text = "No hay pacientes registrados."
                    setPadding(16, 16, 16, 16)
                    textSize = 18f
                }
                listaPacientesLayout.addView(noPacientesText)
            } else {
                // Por cada paciente, crear un TextView y añadirlo al layout
                for (doc in snapshot.documents) {
                    val pacienteId = doc.id
                    val nombrePaciente = doc.getString("email") ?: "Desconocido"

                    // Crear un TextView para cada paciente
                    val pacienteTextView = TextView(this).apply {
                        text = nombrePaciente
                        setPadding(16, 16, 16, 16)
                        textSize = 18f

                        setOnClickListener {
                            // Al hacer clic, pasar el ID del paciente a la actividad de configuración de juegos
                            val intent = Intent(this@ListaPacientesActivity, ConfiguracionJuegosPaciente::class.java)
                            intent.putExtra("PACIENTE_ID", pacienteId)
                            startActivity(intent)
                        }
                    }

                    // Añadir el TextView al layout
                    listaPacientesLayout.addView(pacienteTextView)
                }
            }
        }
    }
}

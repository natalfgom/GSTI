package com.example.gsti.configuracionJuegos

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.JuegosActivity
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class ListaPacientesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val pacientes = mutableListOf<Map<String, String>>() // Lista de pacientes en memoria

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pacientes)

        val listaPacientesLayout: LinearLayout = findViewById(R.id.listaPacientesLayout)
        val buscarPacientesEditText: EditText = findViewById(R.id.buscarPacientesEditText)

        val medicoId = FirebaseAuth.getInstance().currentUser?.email ?: "medico@gmail.com"

        val pacientesRef = db.collection("Medicos").document(medicoId).collection("Pacientes")

        // Obtener pacientes de Firebase
        pacientesRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val noPacientesText = TextView(this).apply {
                    text = "No hay pacientes registrados."
                    setPadding(16, 16, 16, 16)
                    textSize = 18f
                }
                listaPacientesLayout.addView(noPacientesText)
            } else {
                // Guardar pacientes en la lista en memoria
                for (doc in snapshot.documents) {
                    val pacienteId = doc.id
                    val email = doc.getString("email") ?: "Desconocido"
                    pacientes.add(mapOf("id" to pacienteId, "email" to email))
                }

                // Mostrar todos los pacientes inicialmente
                mostrarPacientes(listaPacientesLayout, pacientes)
            }
        }

        // Filtrar pacientes dinámicamente
        buscarPacientesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s.toString().lowercase()

                // Filtrar pacientes por email
                val pacientesFiltrados = pacientes.filter {
                    it["email"]?.lowercase()?.contains(filtro) == true
                }

                // Actualizar la vista de la lista con los pacientes filtrados
                listaPacientesLayout.removeAllViews()
                mostrarPacientes(listaPacientesLayout, pacientesFiltrados)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Método para mostrar pacientes en el layout
    private fun mostrarPacientes(layout: LinearLayout, listaPacientes: List<Map<String, String>>) {
        for (paciente in listaPacientes) {
            val pacienteId = paciente["id"] ?: ""
            val email = paciente["email"] ?: "Desconocido"

            val pacienteContainer = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 16
                    marginEnd = 16
                    topMargin = 16
                    bottomMargin = 16
                }
                setBackgroundResource(R.drawable.border)
                setPadding(16, 16, 16, 16)
            }

            val pacienteTextView = TextView(this).apply {
                text = email
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }

            pacienteContainer.addView(pacienteTextView)

            pacienteContainer.setOnClickListener {
                val intent = Intent(this@ListaPacientesActivity, ConfiguracionJuegosPaciente::class.java)
                intent.putExtra("PACIENTE_ID", pacienteId)
                startActivity(intent)
            }

            layout.addView(pacienteContainer)
        }
    }
}

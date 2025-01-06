package com.example.gsti.configuracionJuegos

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConfiguracionJuegosPaciente : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_juegos)

        val pacienteId = intent.getStringExtra("PACIENTE_ID") ?: return
        val juegosLayout: LinearLayout = findViewById(R.id.configuracionjuegosLayout)

        // TextView para mostrar el email del paciente
        val emailPacienteTextView: TextView = findViewById(R.id.emailPacienteTextView)

        // Obtener los switches para los juegos
        val switchAtencion: Switch = findViewById(R.id.switchAtencion)
        val switchMemoria: Switch = findViewById(R.id.switchMemoria)
        val switchLenguaje: Switch = findViewById(R.id.switchLenguaje)

        // Botón para guardar los cambios
        val btnGuardar: Button = findViewById(R.id.btnGuardar)

        // Obtener el email del médico autenticado
        val medicoId = FirebaseAuth.getInstance().currentUser?.email ?: "medico@gmail.com"
        Log.d("ConfiguracionJuegosPaciente", "ID Médico: $medicoId")

        // Consultar la información del paciente
        val pacienteRef = db.collection("Medicos")
            .document(medicoId) // Documento del médico
            .collection("Pacientes") // Subcolección de pacientes
            .document(pacienteId) // Documento del paciente específico

        Log.d("ConfiguracionJuegosPaciente", "Consultando datos para el paciente con ID: $pacienteId")

        pacienteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener el email del paciente
                val emailPaciente = document.getString("email") ?: "No disponible"
                // Establecer el email en el TextView
                emailPacienteTextView.text = "Email: $emailPaciente"

                // Leer los campos de los juegos
                val juegoAtencionActivo = document.getBoolean("juegoAtencionActivo") ?: false
                val juegoMemoriaActivo = document.getBoolean("juegoMemoriaActivo") ?: false
                val juegoLenguajeActivo = document.getBoolean("juegoLenguajeActivo") ?: false

                // Establecer el estado de los switches
                switchAtencion.isChecked = juegoAtencionActivo
                switchMemoria.isChecked = juegoMemoriaActivo
                switchLenguaje.isChecked = juegoLenguajeActivo

                // Botón para guardar los cambios
                btnGuardar.setOnClickListener {
                    // Obtener los estados de los switches
                    val nuevoEstadoAtencion = switchAtencion.isChecked
                    val nuevoEstadoMemoria = switchMemoria.isChecked
                    val nuevoEstadoLenguaje = switchLenguaje.isChecked

                    // Crear un MutableMap con los nuevos estados
                    val cambios: MutableMap<String, Any> = mutableMapOf(
                        "juegoAtencionActivo" to nuevoEstadoAtencion,
                        "juegoMemoriaActivo" to nuevoEstadoMemoria,
                        "juegoLenguajeActivo" to nuevoEstadoLenguaje
                    )

                    // Guardar los nuevos estados en Firestore

                    // 1. Actualizar la subcolección "Pacientes" dentro del médico
                    pacienteRef.update(cambios).addOnSuccessListener {
                        Log.d("ConfiguracionJuegosPaciente", "Cambios guardados con éxito en subcolección del médico.")
                    }.addOnFailureListener { exception ->
                        Log.e("ConfiguracionJuegosPaciente", "Error al guardar los cambios en la subcolección del médico", exception)
                    }

                    // 2. Actualizar la colección global "Pacientes"
                    val pacienteGlobalRef = db.collection("Pacientes").document(pacienteId)

                    pacienteGlobalRef.update(cambios).addOnSuccessListener {
                        Log.d("ConfiguracionJuegosPaciente", "Cambios guardados con éxito en la colección global de Pacientes.")
                    }.addOnFailureListener { exception ->
                        Log.e("ConfiguracionJuegosPaciente", "Error al guardar los cambios en la colección global de pacientes", exception)
                    }
                }
            } else {
                // Si no se encuentra el paciente
                Log.d("ConfiguracionJuegosPaciente", "No se encontraron datos para el paciente.")
                val noJuegosText = TextView(this).apply {
                    text = "No se encontraron datos de juegos para este paciente."
                    setPadding(16, 16, 16, 16)
                    textSize = 18f
                }
                juegosLayout.addView(noJuegosText)
            }
        }.addOnFailureListener { exception ->
            Log.e("ConfiguracionJuegosPaciente", "Error al consultar datos del paciente: ", exception)
        }
    }
}

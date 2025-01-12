package com.example.gsti.configuracionJuegos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.InformacionPersonal.InformacionMedicoActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.menuInicio.InicioMedico
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Tasks

class ConfiguracionJuegosPaciente : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_juegos)

        val pacienteId = intent.getStringExtra("PACIENTE_ID") ?: return
        val juegosLayout: LinearLayout = findViewById(R.id.configuracionjuegosLayout)

        // TextViews para mostrar los datos del paciente
        val nombrePacienteTextView: TextView = findViewById(R.id.nombrePacienteTextView)
        val emailPacienteTextView: TextView = findViewById(R.id.emailPacienteTextView)
        val birthdayPacienteTextView: TextView = findViewById(R.id.birthdayPacienteTextView)
        val phonePacienteTextView: TextView = findViewById(R.id.phonePacienteTextView)

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
                // Obtener el nombre, apellido, email, fecha de nacimiento y teléfono del paciente
                val nombrePaciente = document.getString("name") ?: "Nombre no disponible"
                val apellidoPaciente = document.getString("surname") ?: "Apellido no disponible"
                val emailPaciente = document.getString("email") ?: "Email no disponible"
                val birthdayPaciente = document.getString("birthday") ?: "Fecha de nacimiento no disponible"
                val phonePaciente = document.getString("phone") ?: "Teléfono no disponible"

                // Establecer los datos en los TextViews
                nombrePacienteTextView.text = "$nombrePaciente $apellidoPaciente"
                emailPacienteTextView.text = "Email: $emailPaciente"
                birthdayPacienteTextView.text = "Fecha de nacimiento: $birthdayPaciente"
                phonePacienteTextView.text = "Teléfono: $phonePaciente"

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
                    val actualizaciones = listOf(
                        pacienteRef.update(cambios), // Subcolección del médico
                        db.collection("Pacientes").document(pacienteId).update(cambios) // Colección global
                    )

                    // Ejecutar todas las actualizaciones
                    Tasks.whenAllComplete(actualizaciones).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ConfiguracionJuegosPaciente", "Cambios guardados con éxito.")
                        } else {
                            Log.e("ConfiguracionJuegosPaciente", "Error al guardar los cambios.")
                        }
                        finish() // Cerrar la actividad después de guardar los cambios
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
        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Inflar el menú desde el archivo XML
        return true
    }

    // Acciones cuando se selecciona un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                startActivity(Intent(this, InicioMedico::class.java))
                true
            }
            R.id.menu_informacion_personal -> {
                // Abrir la actividad de Información Personal
                startActivity(Intent(this, InformacionMedicoActivity::class.java))
                true
            }
            R.id.menu_sobre_nosotros -> {
                // Abrir la actividad Sobre Nosotros
                startActivity(Intent(this, SobreNosotros::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

package com.example.gsti.Estadisticas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.EstadisticasActivity
import com.example.gsti.InformacionPersonal.InformacionMedicoActivity
import com.example.gsti.InformacionPersonal.InformacionPacienteActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.configuracionJuegos.ConfiguracionJuegosPaciente
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListaPacientesEstadisticasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val pacientes = mutableListOf<Map<String, String>>() // Lista de pacientes en memoria

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Asegúrate de que `menu_toolbar` existe
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_listas)

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

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
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
                // Redirigir a EstadisticasPacienteActivity y pasar el email del paciente
                val intent = Intent(this@ListaPacientesEstadisticasActivity, EstadisticasPacienteActivity::class.java)
                intent.putExtra("EMAIL_PACIENTE", email) // Asegúrate de enviar el email correcto
                startActivity(intent)
            }


            layout.addView(pacienteContainer)
        }
    }

    // Acciones al seleccionar un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                val intent = Intent(this, InicioMedico::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_informacion_personal -> {
                // Redirigir a la actividad de información personal
                val intent = Intent(this, InformacionMedicoActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_sobre_nosotros -> {
                // Redirigir a la actividad "Sobre Nosotros"
                val intent = Intent(this, SobreNosotros::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
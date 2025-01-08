package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.Estadisticas.EstadisticasPacienteActivity
import com.example.gsti.InformacionPersonalActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioFamiliar : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnEstadisticas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_familiar) // Referencia al layout

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Botón de "Atrás" si es necesario

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

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)  // Inflar el menú desde el archivo XML
        return true
    }

    // Acciones cuando se selecciona un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                // Redirigir al menú principal según la colección del usuario
                redirigirSegunColeccion()
                true
            }
            R.id.menu_informacion_personal -> {
                // Redirigir a la actividad de información personal
                val intent = Intent(this, InformacionPersonalActivity::class.java)
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

    // Función para redirigir al menú principal según la colección del usuario
    private fun redirigirSegunColeccion() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            db.collection("Medicos").document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val intent = Intent(this, InicioMedico::class.java)
                    startActivity(intent)
                    return@addOnSuccessListener
                }

                db.collection("Pacientes").document(uid).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val intent = Intent(this, InicioPaciente::class.java)
                        startActivity(intent)
                        return@addOnSuccessListener
                    }

                    db.collection("Familiares").document(uid).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val intent = Intent(this, InicioFamiliar::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "No se pudo determinar el tipo de usuario.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}

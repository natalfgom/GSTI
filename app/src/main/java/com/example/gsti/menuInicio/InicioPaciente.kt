package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.EstadisticasActivity
import com.example.gsti.R
import com.example.gsti.juegosPaciente.JuegosPacienteActivity
import com.example.gsti.InformacionPersonalActivity
import com.example.gsti.SobreNosotros
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioPaciente : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_paciente)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Botón de "Atrás" si es necesario

        // Referencias a los botones usando sus IDs
        val btnJuegos = findViewById<LinearLayout>(R.id.btnJuegos)
        val btnEstadisticas = findViewById<LinearLayout>(R.id.btnEstadisticas)
        val btnNotificaciones = findViewById<LinearLayout>(R.id.btnNotificaciones)

        // Acción para "Juegos"
        btnJuegos.setOnClickListener {
            val intent = Intent(this, JuegosPacienteActivity::class.java)
            startActivity(intent)
        }

        // Acción para "Estadísticas"
        btnEstadisticas.setOnClickListener {
            val intent = Intent(this, EstadisticasActivity::class.java)
            startActivity(intent)
        }

        // Acción para "Notificaciones"
        btnNotificaciones.setOnClickListener {
            Toast.makeText(this, "Ir a Notificaciones", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, InformacionPersonalActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_sobre_nosotros -> {
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
            db.collection("medicos").document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val intent = Intent(this, InicioMedico::class.java)
                    startActivity(intent)
                    return@addOnSuccessListener
                }

                db.collection("pacientes").document(uid).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val intent = Intent(this, InicioPaciente::class.java)
                        startActivity(intent)
                        return@addOnSuccessListener
                    }

                    db.collection("familiares").document(uid).get().addOnSuccessListener { document ->
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

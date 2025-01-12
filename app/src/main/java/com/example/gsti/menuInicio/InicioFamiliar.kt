package com.example.gsti.menuInicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.Estadisticas.EstadisticasPacienteActivity
import com.example.gsti.Estadisticas.ListaPacientesEstadisticasActivity
import com.example.gsti.InformacionPersonal.InformacionFamiliarActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioFamiliar : AppCompatActivity() {

    private lateinit var btnEstadisticas: LinearLayout
    private lateinit var btnCambiarPin: LinearLayout

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_familiar)

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Botón de "Atrás" si es necesario

        // Referencias a botones usando sus IDs
        btnEstadisticas = findViewById(R.id.btnEstadisticas)
        btnCambiarPin = findViewById(R.id.btnNotificaciones)

        // Obtener el paciente asociado del intent
        val pacienteAsociado = intent.getStringExtra("PACIENTE_ASOCIADO")

        // Acción para el botón "Estadísticas"
        btnEstadisticas.setOnClickListener {
            if (pacienteAsociado != null) {
                val intent = Intent(this, EstadisticasPacienteActivity::class.java)
                intent.putExtra("EMAIL_PACIENTE", pacienteAsociado)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "No se pudo encontrar el paciente asociado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Botón Notificaciones
        btnCambiarPin.setOnClickListener {
            startActivity(Intent(this, CambiarPin::class.java))        }
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)  // Asegúrate de que `menu_toolbar` existe
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
                val intent = Intent(this, InformacionFamiliarActivity::class.java)
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

package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.Estadisticas.ListaPacientesEstadisticasActivity
import com.example.gsti.InformacionPersonal.InformacionMedicoActivity
import com.example.gsti.InformacionPersonalActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.configuracionJuegos.ListaPacientesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioMedico : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Declaramos las variables para los botones
    private lateinit var btnConfiguracionJuegos: LinearLayout
    private lateinit var btnEstadisticas: LinearLayout
    private lateinit var btnNotificaciones: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_medico)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar vistas
        btnConfiguracionJuegos = findViewById(R.id.btnConfiguracionJuegos)
        btnEstadisticas = findViewById(R.id.btnEstadisticas)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)

        // Configurar listeners para los botones
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        // Botón Configuración de Juegos
        btnConfiguracionJuegos.setOnClickListener {
            startActivity(Intent(this, ListaPacientesActivity::class.java))
        }

        // Botón Estadísticas
        btnEstadisticas.setOnClickListener {
            startActivity(Intent(this, ListaPacientesEstadisticasActivity::class.java))
        }

        // Botón Notificaciones
        btnNotificaciones.setOnClickListener {
            Toast.makeText(this, "Notificaciones seleccionada", Toast.LENGTH_SHORT).show()
        }
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
                redirigirSegunColeccion() // Redirigir al menú principal
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

    // Función para redirigir al menú principal según la colección del usuario
    private fun redirigirSegunColeccion() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = user.uid

        db.collection("Medicos").document(uid).get()
            .addOnSuccessListener { document ->
                when {
                    document.exists() -> startActivity(Intent(this, InicioMedico::class.java))
                    else -> redirigirPacienteOFamiliar(uid)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al consultar la base de datos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun redirigirPacienteOFamiliar(uid: String) {
        db.collection("Pacientes").document(uid).get()
            .addOnSuccessListener { document ->
                when {
                    document.exists() -> startActivity(Intent(this, InicioPaciente::class.java))
                    else -> redirigirFamiliar(uid)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al consultar la base de datos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun redirigirFamiliar(uid: String) {
        db.collection("Familiares").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    startActivity(Intent(this, InicioFamiliar::class.java))
                } else {
                    Toast.makeText(this, "No se pudo determinar el tipo de usuario.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al consultar la base de datos.", Toast.LENGTH_SHORT).show()
            }
    }
}

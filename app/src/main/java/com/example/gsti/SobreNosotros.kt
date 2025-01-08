package com.example.gsti

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SobreNosotros : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sobre_nosotros) // Referencia al layout

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Botón de "Atrás" si es necesario
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
                // Redirigir a la actividad "Sobre Nosotros" (actualmente en la misma pantalla)
                Toast.makeText(this, "Ya estás en la pantalla Sobre Nosotros", Toast.LENGTH_SHORT).show()
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
            // Check for the 'medicos' collection first
            db.collection("Medicos").document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the user is a doctor, navigate to InicioMedico
                    val intent = Intent(this, InicioMedico::class.java)
                    startActivity(intent)
                    return@addOnSuccessListener
                }
                // Check for the 'pacientes' collection if not found in 'medicos'
                db.collection("Pacientes").document(uid).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        // If the user is a patient, navigate to InicioPaciente
                        val intent = Intent(this, InicioPaciente::class.java)
                        startActivity(intent)
                        return@addOnSuccessListener
                    }
                    // Check for the 'familiares' collection if not found in 'pacientes'
                    db.collection("Familiares").document(uid).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            // If the user is a family member, navigate to InicioFamiliar
                            val intent = Intent(this, InicioFamiliar::class.java)
                            startActivity(intent)
                        } else {
                            // If no valid collection is found
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

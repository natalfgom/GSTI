package com.example.gsti.InformacionPersonal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.AuthActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.menuInicio.InicioPaciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InformacionPacienteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_personal)

        // Referencias a los TextViews y EditText
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val surnameTextView: TextView = findViewById(R.id.surnameTextView)
        val birthdayTextView: TextView = findViewById(R.id.birthdayTextView)
        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val logoutButton: Button = findViewById(R.id.logoutButton) // Referencia al botón de cerrar sesión

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Cerrar sesión al presionar el botón
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada correctamente.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java) // Reemplaza "LoginActivity" con tu actividad de login
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Cierra la actividad actual
        }

        // Obtener el email del paciente y cargar los datos (código previo permanece igual)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val paciEmail = currentUser?.email

        // Resto del código de carga de datos y actualización de Firestore permanece igual...
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // Acciones al seleccionar un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                val intent = Intent(this, InicioPaciente::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                true
            }
            R.id.menu_informacion_personal -> {
                val intent = Intent(this, InformacionPacienteActivity::class.java)
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
}

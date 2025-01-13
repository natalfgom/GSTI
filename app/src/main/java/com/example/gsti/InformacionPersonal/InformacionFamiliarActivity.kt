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
import com.example.gsti.menuInicio.InicioFamiliar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class InformacionFamiliarActivity : AppCompatActivity() {

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
        val logoutButton: Button = findViewById(R.id.logoutButton) // Botón de cerrar sesión

        // Configurar el toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Obtener el email del usuario logueado
        val currentUser = FirebaseAuth.getInstance().currentUser
        val familiarEmail = currentUser?.email

        if (familiarEmail != null) {
            // Consultar el documento del familiar por email
            db.collection("Familiares").document(familiarEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "No disponible"
                        val surname = document.getString("surname") ?: "No disponible"
                       val email = document.getString("email") ?: "No disponible"
                        val phone = document.getString("phone") ?: "No disponible"

                        // Obtener el campo 'birthday' como Timestamp
                        val birthdayTimestamp = document.getTimestamp("birthday")

// Verificar si el valor no es nulo y formatear la fecha
                        val birthday = if (birthdayTimestamp != null) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.format(birthdayTimestamp.toDate()) // Convertir Timestamp a Date y formatear
                        } else {
                            "No disponible"
                        }


                        // Mostrar los datos en los TextViews
                        nameTextView.text = "Nombre: $name"
                        surnameTextView.text = "Apellido: $surname"
                        birthdayTextView.text = "Fecha de Nacimiento: $birthday"
                        emailTextView.text = "Email: $email"
                        phoneEditText.setText(phone)
                    } else {
                        // Documento no encontrado
                        nameTextView.text = "No se encontró información del familiar."
                        surnameTextView.text = ""
                        birthdayTextView.text = ""
                        emailTextView.text = ""
                        phoneEditText.setText("")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener los datos.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Usuario no autenticado o email no disponible
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }

        // Botón para guardar cambios
        saveButton.setOnClickListener {
            val updatedPhone = phoneEditText.text.toString()

            if (updatedPhone.isNotEmpty()) {
                // Actualizar el teléfono en Firestore
                val updatedData = mapOf("phone" to updatedPhone)

                db.collection("Familiares").document(familiarEmail!!)
                    .update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show()
                        finish() // Regresar a la actividad anterior
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar los datos.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, complete el campo de teléfono.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para cerrar sesión
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada correctamente.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java) // Reemplaza "LoginActivity" con tu actividad de login
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Cierra la actividad actual
        }
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Asegúrate de que `menu_toolbar` existe
        return true
    }

    // Acciones al seleccionar un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                // Redirigir al menú principal
                val intent = Intent(this, InicioFamiliar::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.menu_informacion_personal -> {
                // Permanecer en esta actividad
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

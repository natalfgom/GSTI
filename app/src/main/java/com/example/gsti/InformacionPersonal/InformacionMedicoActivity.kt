package com.example.gsti.InformacionPersonal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InformacionMedicoActivity : AppCompatActivity() {

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

        // Obtener el email del usuario logueado
        val currentUser = FirebaseAuth.getInstance().currentUser
        val medicoEmail = currentUser?.email

        if (medicoEmail != null) {
            // Consultar el documento del médico por email
            db.collection("Medicos").document(medicoEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "No disponible"
                        val surname = document.getString("surname") ?: "No disponible"
                        val birthday = document.getString("birthday") ?: "No disponible"
                        val email = document.getString("email") ?: "No disponible"
                        val phone = document.getString("phone") ?: "No disponible"

                        // Mostrar los datos en los TextViews
                        nameTextView.text = "Nombre: $name"
                        surnameTextView.text = "Apellido: $surname"
                        birthdayTextView.text = "Fecha de Nacimiento: $birthday"
                        emailTextView.text = "Email: $email"
                        phoneEditText.setText(phone)
                    } else {
                        // Documento no encontrado
                        nameTextView.text = "No se encontró información del médico."
                        surnameTextView.text = ""
                        birthdayTextView.text = ""
                        emailTextView.text = ""
                        phoneEditText.setText("")
                    }
                }
                .addOnFailureListener { exception ->
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

                db.collection("Medicos").document(medicoEmail!!)
                    .update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show()
                        finish() // Regresar a la actividad anterior
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al actualizar los datos.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, complete el campo de teléfono.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

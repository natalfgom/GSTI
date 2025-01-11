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

        // Obtener el email del paciente (usuario logueado)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val paciEmail = currentUser?.email

        if (paciEmail != null) {
            // Consultar el documento del paciente por email
            db.collection("Pacientes").document(paciEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "No disponible"
                        val surname = document.getString("surname") ?: "No disponible"
                        val birthday = document.getString("birthday") ?: "No disponible"
                        val email = document.getString("email") ?: "No disponible"
                        val phone = document.getString("phone") ?: "No disponible"
                        val doctorEmail = document.getString("doctor") ?: "No disponible" // Obtener el email del médico

                        // Mostrar los datos en los TextViews
                        nameTextView.text = "Nombre: $name"
                        surnameTextView.text = "Apellido: $surname"
                        birthdayTextView.text = "Fecha de Nacimiento: $birthday"
                        emailTextView.text = "Email: $email"
                        phoneEditText.setText(phone)

                        // Guardar los cambios cuando el usuario presiona el botón "Guardar"
                        saveButton.setOnClickListener {
                            val updatedPhone = phoneEditText.text.toString()

                            if (updatedPhone.isNotEmpty()) {
                                // Actualizar el teléfono en Firestore (en la colección Pacientes)
                                val updatedData = mapOf("phone" to updatedPhone)

                                // Actualizar en la colección global "Pacientes"
                                db.collection("Pacientes").document(paciEmail)
                                    .update(updatedData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Datos actualizados correctamente en Pacientes.", Toast.LENGTH_SHORT).show()

                                        // Además, actualizar en la subcolección "Pacientes" del médico
                                        db.collection("Medicos").document(doctorEmail)
                                            .collection("Pacientes").document(paciEmail)
                                            .update(updatedData)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Datos actualizados correctamente en la subcolección del Médico.", Toast.LENGTH_SHORT).show()
                                                finish() // Regresar a la actividad anterior
                                            }
                                            .addOnFailureListener { exception ->
                                                Toast.makeText(this, "Error al actualizar en la subcolección del médico.", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Error al actualizar los datos en la colección Pacientes.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Por favor, complete el campo de teléfono.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Documento no encontrado
                        nameTextView.text = "No se encontró información del paciente."
                        surnameTextView.text = ""
                        birthdayTextView.text = ""
                        emailTextView.text = ""
                        phoneEditText.setText("")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al obtener los datos del paciente.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Usuario no autenticado o email no disponible
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}

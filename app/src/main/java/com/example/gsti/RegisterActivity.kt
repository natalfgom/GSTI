package com.example.gsti

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicializar vistas
        val emailField: EditText = findViewById(R.id.emailField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val nameField: EditText = findViewById(R.id.nameField)
        val surnameField: EditText = findViewById(R.id.surnameField)
        val birthdayField: EditText = findViewById(R.id.birthdayField)
        val phoneField: EditText = findViewById(R.id.phoneField)
        val typeGroup: RadioGroup = findViewById(R.id.typeGroup)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val name = nameField.text.toString().trim()
            val surname = surnameField.text.toString().trim()
            val birthdayText = birthdayField.text.toString().trim()
            val phoneText = phoneField.text.toString().trim()
            val selectedTypeId = typeGroup.checkedRadioButtonId
            val type = findViewById<RadioButton>(selectedTypeId)?.text.toString()

            // Validación de campos
            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() && birthdayText.isNotEmpty() && phoneText.isNotEmpty() && type.isNotEmpty()) {

                // Validar formato de fecha
                val birthday: Date? = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthdayText)
                } catch (e: Exception) {
                    Toast.makeText(this, "Formato de fecha incorrecto. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validar número de teléfono
                val phone: Int? = phoneText.toIntOrNull()
                if (phone == null || phoneText.length != 9) {
                    Toast.makeText(this, "Número de teléfono inválido. Debe contener 9 dígitos.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Registrar usuario en Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Guardar datos adicionales en Firestore
                            val userMap = hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "birthday" to birthday,
                                "phone" to phone,
                                "type" to type,
                                "email" to email
                            )

                            val collection = when (type) {
                                getString(R.string.patient) -> "Pacientes"
                                getString(R.string.family) -> "Familiares"
                                getString(R.string.doctor) -> "Medicos"
                                else -> "Usuarios"
                            }

                            firestore.collection(collection).document(email)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

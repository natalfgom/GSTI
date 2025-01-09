package com.example.gsti

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gsti.R
import com.example.gsti.RegisterActivity
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var btnRegistro: Button
    private lateinit var btnAcceder: Button
    private lateinit var email: EditText
    private lateinit var passwd: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Inicializar las vistas
        btnRegistro = findViewById(R.id.btnRegistro)
        btnAcceder = findViewById(R.id.btnAcceder)
        email = findViewById(R.id.emailEditText)
        passwd = findViewById(R.id.passwdEditText)

        // Configurar la lógica para los botones
        setup()
    }

    private fun setup() {
        // Lógica para el botón de Registro
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Lógica para el botón de Acceder
        btnAcceder.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = passwd.text.toString().trim()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                            showHome(emailText)
                        } else {
                            showAlert("Error al iniciar sesión: ${task.exception?.message}")
                        }
                    }
            } else {
                showAlert("Por favor, complete todos los campos")
            }
        }
    }

    private fun showHome(email: String) {
        Firebase.firestore.collection("Medicos").document(email).get()
            .addOnSuccessListener { medicoSnapshot ->
                if (medicoSnapshot.exists()) {
                    // Guardar el rol en SharedPreferences y redirigir al inicio del médico
                    saveUserRole("medico")
                    val intent = Intent(this, InicioMedico::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Firebase.firestore.collection("Familiares").document(email).get()
                        .addOnSuccessListener { familiarSnapshot ->
                            if (familiarSnapshot.exists()) {
                                // Guardar el rol en SharedPreferences y redirigir al inicio del familiar
                                saveUserRole("familiar")
                                val intent = Intent(this, InicioFamiliar::class.java)
                                intent.putExtra("PACIENTE_ASOCIADO", familiarSnapshot.getString("patient"))
                                startActivity(intent)
                                finish()
                            } else {
                                Firebase.firestore.collection("Medicos").get()
                                    .addOnSuccessListener { result ->
                                        var pacienteEncontrado = false
                                        for (medicoDoc in result) {
                                            val medicoEmail = medicoDoc.id

                                            Firebase.firestore.collection("Medicos")
                                                .document(medicoEmail)
                                                .collection("Pacientes")
                                                .document(email)
                                                .get()
                                                .addOnSuccessListener { pacienteSnapshot ->
                                                    if (pacienteSnapshot.exists()) {
                                                        // Guardar el rol en SharedPreferences y redirigir al inicio del paciente
                                                        saveUserRole("paciente")
                                                        val intent = Intent(this, InicioPaciente::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                        pacienteEncontrado = true
                                                    }
                                                }

                                            if (pacienteEncontrado) {
                                                return@addOnSuccessListener
                                            }
                                        }

                                        if (!pacienteEncontrado) {
                                            showAlert("Usuario no encontrado en la base de datos.")
                                        }
                                    }
                            }
                        }
                }
            }
    }

    // Guardar el rol del usuario en SharedPreferences
    private fun saveUserRole(role: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_ROLE", role)
        editor.apply()
    }

    // Función para mostrar alertas
    private fun showAlert(text: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
            .setMessage(text)
            .setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    // Función para limpiar los campos de texto
    private fun limpiarCampos() {
        email.text.clear()
        passwd.text.clear()
    }
}

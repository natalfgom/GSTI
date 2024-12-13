package com.example.gsti

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

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
            val emailText = email.text.toString().trim()
            val passwordText = passwd.text.toString().trim()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                            showHome(emailText)
                        } else {
                            showAlert("Error al registrar: ${task.exception?.message}")
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
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
        Log.d("AuthActivity", "Redirigiendo al usuario: $email")

        Firebase.firestore.collection("Medicos").document(email).get().addOnSuccessListener {
            if(it.exists()){
                Log.d("AuthActivity", "Redirigiendo a HomeMedicoActivity")
                val intent = Intent(this, InicioMedico::class.java)
                startActivity(intent)
                finish()

            }
            else{
                Firebase.firestore.collection("Familiares").document(email).get().addOnSuccessListener {
                    if(it.exists()){
                        Log.d("AuthActivity", "Redirigiendo a HomeMedicoActivity")
                        val intent = Intent(this, InicioFamiliar::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else{
                        Log.d("AuthActivity", "Redirigiendo a HomePacienteActivity")
                        val intent = Intent(this, InicioPaciente::class.java)
                        startActivity(intent)
                        finish()

                    }
                }

            }
        }



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

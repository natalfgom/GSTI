package com.example.gsti.juegoMemoria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.OrientacionActivity
import com.example.gsti.R
import com.google.firebase.firestore.FirebaseFirestore

class ComprensionMemoriaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comprension)

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        val codeField: EditText = findViewById(R.id.codeField)
        val yesButton: Button = findViewById(R.id.yesButton)
        val noButton: Button = findViewById(R.id.buttonNo)

        // Configurar el botón "Sí"
        yesButton.setOnClickListener {
            val enteredCode = codeField.text.toString().trim()

            if (enteredCode.length != 4) {
                Toast.makeText(this, "El código debe tener 4 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar el código en Firestore
            firestore.collection("Familiares")
                .whereEqualTo("confirmationCode", enteredCode)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        // Código válido: avanzar a la siguiente actividad
                        Toast.makeText(this, "Código válido, avanzando...", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MecanicaMemoria::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Código inválido
                        Toast.makeText(this, "Código inválido, intente de nuevo", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al verificar el código: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Configurar el botón "No"
        noButton.setOnClickListener {
            // Redirigir a la página de orientación
            val intent = Intent(this, OrientacionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
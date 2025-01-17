package com.example.gsti.menuInicio

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.InformacionPersonal.InformacionFamiliarActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference

class CambiarPin : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etEmail: EditText
    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnUpdatePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_confirmation_code)

        // Configurar el toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar Firebase Authentication y Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a los componentes de la UI
        etEmail = findViewById(R.id.et_email)
        etOldPassword = findViewById(R.id.et_old_password)
        etNewPassword = findViewById(R.id.et_new_password)
        btnUpdatePassword = findViewById(R.id.btn_update_password)

        // Accionar el botón para actualizar la contraseña
        btnUpdatePassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val oldPassword = etOldPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()

            // Verificar que los campos no estén vacíos
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar las credenciales
            authenticateUser(email, oldPassword, newPassword)
        }
    }

    // Método para autenticar al usuario con la contraseña antigua
    private fun authenticateUser(email: String, oldPassword: String, newPassword: String) {
        mAuth.signInWithEmailAndPassword(email, oldPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si la autenticación es exitosa, cambiar el confirmationCode
                    changeConfirmationCode(email, newPassword)
                } else {
                    // Si la contraseña antigua no es correcta
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Contraseña antigua incorrecta", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al autenticar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Método para cambiar el confirmationCode
    private fun changeConfirmationCode(email: String, newConfirmationCode: String) {
        // Obtener la referencia al documento del paciente en la colección Familiares
        val docRef: DocumentReference = db.collection("Familiares").document(email)

        // Actualizar el campo confirmationCode
        docRef.update("confirmationCode", newConfirmationCode)
            .addOnSuccessListener {
                // Si se actualizó correctamente
                Toast.makeText(this, "Código de confirmación actualizado correctamente", Toast.LENGTH_SHORT).show()
                etOldPassword.setText("")  // Limpiar el campo de la contraseña antigua
                etNewPassword.setText("")  // Limpiar el campo de la nueva contraseña

                // Cerrar la actividad actual y volver a la anterior
                finish()
            }
            .addOnFailureListener { e ->
                // Si hubo un error
                Toast.makeText(this, "Error al actualizar el código: ${e.message}", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, InformacionFamiliarActivity::class.java)
                startActivity(intent)
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

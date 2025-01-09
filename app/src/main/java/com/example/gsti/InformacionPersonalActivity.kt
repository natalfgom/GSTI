package com.example.gsti

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.example.gsti.menuInicio.InicioPaciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InformacionPersonalActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var fechaNacimientoTextView: TextView

    // Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de "Atrás"

        // Asignar vistas a variables
        nombreEditText = findViewById(R.id.nombre)
        apellidosEditText = findViewById(R.id.apellidos)
        telefonoEditText = findViewById(R.id.telefono)
        emailTextView = findViewById(R.id.email)
        fechaNacimientoTextView = findViewById(R.id.fecha_nacimiento)

        // Cargar datos del usuario desde Firebase
        cargarDatosUsuario()

        // Configurar botón para actualizar la información
        val botonActualizar: Button = findViewById(R.id.actualizar)
        botonActualizar.setOnClickListener {
            actualizarInformacion(it)
        }
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Inflar el menú desde XML
        return true
    }

    // Manejar acciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                // Redirigir según el rol del usuario almacenado en SharedPreferences
                redirigirAlMenuPrincipal()
                true
            }
            R.id.menu_informacion_personal -> {
                Toast.makeText(this, "Ya estás en Información Personal", Toast.LENGTH_SHORT).show()
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

    // Función para redirigir al menú principal según el rol
    private fun redirigirAlMenuPrincipal() {
        val userRole = getUserRole()

        when (userRole) {
            "medico" -> {
                val intent = Intent(this, InicioMedico::class.java)
                startActivity(intent)
            }
            "paciente" -> {
                val intent = Intent(this, InicioPaciente::class.java)
                startActivity(intent)
            }
            "familiar" -> {
                val intent = Intent(this, InicioFamiliar::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "No se pudo determinar el tipo de usuario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Obtener el rol del usuario desde SharedPreferences
    private fun getUserRole(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ROLE", null)
    }

    // Cargar datos del usuario desde Firebase
    private fun cargarDatosUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (email != null) {
            buscarUsuarioEnColecciones(email)
        } else {
            Log.e("FirebaseAuth", "No se pudo obtener el email del usuario autenticado")
            Toast.makeText(this, "Error al obtener email del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarUsuarioEnColecciones(email: String) {
        val colecciones = listOf("Medicos", "Pacientes", "Familiares")
        var encontrado = false

        for (coleccion in colecciones) {
            if (encontrado) break

            db.collection(coleccion)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        encontrado = true
                        Log.d("Firestore", "Usuario encontrado en la colección $coleccion")
                        val documento = querySnapshot.documents[0]
                        val nombre = documento.getString("nombre")
                        val apellidos = documento.getString("apellidos")
                        val telefono = documento.getString("telefono")
                        val fechaNacimiento = documento.getString("fechaNacimiento")

                        // Mostrar los datos en los campos correspondientes
                        nombreEditText.setText(nombre)
                        apellidosEditText.setText(apellidos)
                        telefonoEditText.setText(telefono)
                        emailTextView.text = email
                        fechaNacimientoTextView.text = fechaNacimiento
                    } else {
                        Log.d("Firestore", "No se encontraron datos en la colección $coleccion")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error al consultar la colección $coleccion: ${exception.message}")
                }
        }
    }


    // Actualizar datos del usuario en Firestore
    fun actualizarInformacion(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (email != null) {
            val nombre = nombreEditText.text.toString()
            val apellidos = apellidosEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
                return
            }

            val datosActualizados = hashMapOf(
                "nombre" to nombre,
                "apellidos" to apellidos,
                "telefono" to telefono
            )

            // Actualizar datos en la colección correspondiente
            val colecciones = listOf("Medicos", "Pacientes", "Familiares")

            for (coleccion in colecciones) {
                db.collection(coleccion)
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documento = querySnapshot.documents[0]
                            val idDocumento = documento.id
                            db.collection(coleccion).document(idDocumento)
                                .update(datosActualizados as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Información actualizada correctamente.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firestore", "Error al actualizar información", exception)
                                    Toast.makeText(this, "Error al actualizar la información.", Toast.LENGTH_SHORT).show()
                                }
                            return@addOnSuccessListener
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error al buscar documento para actualizar", exception)
                    }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}

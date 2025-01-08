package com.example.gsti

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

    // Inicializar Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de "Atrás" si es necesario

        // Asignación de vistas a variables
        nombreEditText = findViewById(R.id.nombre)
        apellidosEditText = findViewById(R.id.apellidos)
        telefonoEditText = findViewById(R.id.telefono)
        emailTextView = findViewById(R.id.email)
        fechaNacimientoTextView = findViewById(R.id.fecha_nacimiento)

        // Cargar la información del usuario desde Firestore
        cargarDatosUsuario()

        // Configurar el botón para actualizar la información
        val botonActualizar: Button = findViewById(R.id.actualizar)
        botonActualizar.setOnClickListener {
            actualizarInformacion(it)
        }
    }

    // Inflar el menú en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)  // Inflar el menú desde el archivo XML
        return true
    }

    // Manejo de las acciones de los ítems del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                // Redirigir dinámicamente según la colección del usuario
                redirigirSegunColeccion()
                true
            }
            R.id.menu_informacion_personal -> {
                // Ya estás en esta pantalla
                Toast.makeText(this, "Ya estás en Información Personal", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_sobre_nosotros -> {
                // Redirigir a la actividad Sobre Nosotros
                val intent = Intent(this, SobreNosotros::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Función para redirigir según la colección en Firestore
    private fun redirigirSegunColeccion() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            // Consultar si el UID existe en la colección "medicos"
            db.collection("Medicos").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Es médico, redirigir a InicioMedico
                        val intent = Intent(this, InicioMedico::class.java)
                        startActivity(intent)
                    } else {
                        // Si no está en "medicos", verificar "pacientes"
                        db.collection("Pacientes").document(uid)
                            .get()
                            .addOnSuccessListener { documentPaciente ->
                                if (documentPaciente.exists()) {
                                    // Es paciente, redirigir a InicioPaciente
                                    val intent = Intent(this, InicioPaciente::class.java)
                                    startActivity(intent)
                                } else {
                                    // Si no está en "pacientes", verificar "familiares"
                                    db.collection("Familiares").document(uid)
                                        .get()
                                        .addOnSuccessListener { documentFamiliar ->
                                            if (documentFamiliar.exists()) {
                                                // Es familiar, redirigir a InicioFamiliar
                                                val intent = Intent(this, InicioFamiliar::class.java)
                                                startActivity(intent)
                                            } else {
                                                // Si no pertenece a ninguna colección, mostrar mensaje
                                                Toast.makeText(
                                                    this,
                                                    "No se encontró información del usuario",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("Firestore", "Error verificando familiares", exception)
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error verificando pacientes", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error verificando médicos", exception)
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para cargar datos de Firestore y mostrarlos en los EditText y TextView
    private fun cargarDatosUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre")
                        val apellidos = document.getString("apellidos")
                        val telefono = document.getString("telefono")
                        val email = document.getString("email")
                        val fechaNacimiento = document.getTimestamp("fechaNacimiento")

                        nombreEditText.setText(nombre)
                        apellidosEditText.setText(apellidos)
                        telefonoEditText.setText(telefono)
                        emailTextView.text = email
                        fechaNacimientoTextView.text = fechaNacimiento?.toDate()?.toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error al obtener documento", exception)
                }
        }
    }

    // Función para actualizar la información del usuario en Firestore
    fun actualizarInformacion(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val nombre = nombreEditText.text.toString()
            val apellidos = apellidosEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return
            }

            val usuarioActualizado: MutableMap<String, Any> = hashMapOf(
                "nombre" to nombre,
                "apellidos" to apellidos,
                "telefono" to telefono
            )

            db.collection("usuarios").document(uid)
                .update(usuarioActualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Locale

class InformacionPersonalActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var fechaNacimientoTextView: TextView
    private lateinit var botonActualizar: Button

    // Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de "Atrás"

//        // Asignar vistas a variables
//        nombreEditText = findViewById(R.id.nombre)
//        apellidosEditText = findViewById(R.id.apellidos)
//        telefonoEditText = findViewById(R.id.telefono)
//        emailTextView = findViewById(R.id.email)
//        fechaNacimientoTextView = findViewById(R.id.fecha_nacimiento)
//        botonActualizar = findViewById(R.id.actualizar)

        // Cargar datos del usuario desde Firebase
        cargarDatosUsuario()

        // Configurar botón para actualizar la información
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

    // Redirigir al menú principal según el rol del usuario
    private fun redirigirAlMenuPrincipal() {
        val userRole = getUserRole()

        when (userRole) {
            "medico" -> startActivity(Intent(this, InicioMedico::class.java))
            "paciente" -> startActivity(Intent(this, InicioPaciente::class.java))
            "familiar" -> startActivity(Intent(this, InicioFamiliar::class.java))
            else -> Toast.makeText(this, "No se pudo determinar el tipo de usuario.", Toast.LENGTH_SHORT).show()
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
        val emailUsuario = user?.email

        if (emailUsuario != null) {
            buscarUsuarioEnSubcoleccion(emailUsuario)
        } else {
            Log.e("FirebaseAuth", "No se pudo obtener el email del usuario autenticado")
            Toast.makeText(this, "Error al obtener email del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    // Buscar al usuario en la subcolección
    private fun buscarUsuarioEnSubcoleccion(emailUsuario: String) {
        val medicoEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val pacienteRef = db.collection("Medicos")
            .document(medicoEmail)
            .collection("Pacientes")
            .whereEqualTo("email", emailUsuario)

        pacienteRef.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documento = querySnapshot.documents[0]
                mostrarDatosUsuario(documento)
            } else {
                Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error al consultar datos: ", exception)
        }
    }

    // Mostrar datos del usuario en los campos
    private fun mostrarDatosUsuario(document: DocumentSnapshot) {
        val nombre = document.getString("nombre") ?: "Nombre no disponible"
        val apellidos = document.getString("apellidos") ?: "Apellidos no disponibles"
        val telefono = document.getString("telefono") ?: "Teléfono no disponible"
        val fechaNacimientoTimestamp = document.getTimestamp("fechaNacimiento")

        // Convertir Timestamp a formato de fecha legible
        val fechaNacimiento = if (fechaNacimientoTimestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(fechaNacimientoTimestamp.toDate())
        } else {
            "Fecha no disponible"
        }

        nombreEditText.setText(nombre)
        apellidosEditText.setText(apellidos)
        telefonoEditText.setText(telefono)
        emailTextView.text = document.getString("email")
        fechaNacimientoTextView.text = fechaNacimiento
    }

    // Actualizar datos del usuario en Firestore
    private fun actualizarInformacion(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val emailUsuario = user?.email

        if (emailUsuario != null) {
            val nombre = nombreEditText.text.toString()
            val apellidos = apellidosEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
                return
            }

            val datosActualizados = mapOf(
                "nombre" to nombre,
                "apellidos" to apellidos,
                "telefono" to telefono
            )

            botonActualizar.isEnabled = false // Deshabilitar botón durante la operación

            // Actualizar datos en Firestore
            val medicoEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
            val pacienteRef = db.collection("Medicos")
                .document(medicoEmail)
                .collection("Pacientes")
                .whereEqualTo("email", emailUsuario)

            pacienteRef.get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents[0]
                    val actualizaciones = listOf(
                        db.collection("Medicos")
                            .document(medicoEmail)
                            .collection("Pacientes")
                            .document(documento.id)
                            .update(datosActualizados)
                    )

                    Tasks.whenAllComplete(actualizaciones).addOnCompleteListener { task ->
                        botonActualizar.isEnabled = true // Rehabilitar botón
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Información actualizada correctamente.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al actualizar información.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                botonActualizar.isEnabled = true // Rehabilitar botón en caso de error
                Log.e("Firestore", "Error al actualizar datos: ", exception)
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}

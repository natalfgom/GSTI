package com.example.gsti

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val pacienteContainer: LinearLayout = findViewById(R.id.pacienteContainer)
        val familiarContainer: LinearLayout = findViewById(R.id.familiarContainer)
        val doctorSpinner: Spinner = findViewById(R.id.doctorSpinner)
        val patientSpinner: Spinner = findViewById(R.id.patientSpinner)
        val confirmationCodeField: EditText = findViewById(R.id.confirmationCodeField)
        val registerButton: Button = findViewById(R.id.registerButton)

        // Mostrar u ocultar campos dinámicos según el rol seleccionado
        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioPaciente -> {
                    pacienteContainer.visibility = View.VISIBLE
                    familiarContainer.visibility = View.GONE
                    loadDoctorsSpinner(doctorSpinner) // Cargar médicos en Spinner
                }
                R.id.radioFamiliar -> {
                    pacienteContainer.visibility = View.GONE
                    familiarContainer.visibility = View.VISIBLE
                    loadPatientsSpinner(patientSpinner) // Cargar pacientes en Spinner
                }
                else -> {
                    pacienteContainer.visibility = View.GONE
                    familiarContainer.visibility = View.GONE
                }
            }
        }

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val name = nameField.text.toString().trim()
            val surname = surnameField.text.toString().trim()
            val birthday = birthdayField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            val selectedTypeId = typeGroup.checkedRadioButtonId
            val type = findViewById<RadioButton>(selectedTypeId)?.text.toString()

            // Validar campos básicos
            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() && birthday.isNotEmpty() && phone.isNotEmpty() && type.isNotEmpty()) {
                val additionalData = hashMapOf<String, Any>(
                    "name" to name,
                    "surname" to surname,
                    "birthday" to birthday,
                    "phone" to phone,
                    "type" to type,
                    "email" to email,
                    "juegoMemoriaActivo" to false,
                    "juegoLenguajeActivo" to false,
                    "juegoAtencionActivo" to false
                )

                // Validar campos adicionales según el rol
                when (type) {
                    getString(R.string.patient) -> {
                        if (doctorSpinner.adapter == null || doctorSpinner.selectedItem == null) {
                            Toast.makeText(this, "Por favor, selecciona un médico.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val selectedDoctor = doctorSpinner.selectedItem.toString()
                        additionalData["doctor"] = selectedDoctor
                    }
                    getString(R.string.family) -> {
                        if (patientSpinner.adapter == null || patientSpinner.selectedItem == null) {
                            Toast.makeText(this, "Por favor, selecciona un paciente.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val selectedPatient = patientSpinner.selectedItem.toString()
                        val confirmationCode = confirmationCodeField.text.toString().trim()

                        if (confirmationCode.length != 4) {
                            Toast.makeText(this, "El código de confirmación debe tener 4 dígitos", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        additionalData["patient"] = selectedPatient
                        additionalData["confirmationCode"] = confirmationCode
                    }
                }

                // Registrar usuario en Firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val collection = when (type) {
                                getString(R.string.patient) -> "Pacientes"
                                getString(R.string.family) -> "Familiares"
                                getString(R.string.doctor) -> "Medicos"
                                else -> "Usuarios"
                            }

                            if (type == getString(R.string.patient)) {
                                // Guardar en la colección Pacientes y crear subcolección en Médicos
                                firestore.collection("Pacientes").document(email)
                                    .set(additionalData)
                                    .addOnSuccessListener {
                                        val selectedDoctor = additionalData["doctor"] as String
                                        firestore.collection("Medicos").document(selectedDoctor)
                                            .collection("Pacientes")
                                            .document(email)
                                            .set(additionalData)
                                            .addOnSuccessListener {
                                                // Crear subcolección Estadisticas vacía
                                                firestore.collection("Pacientes").document(email)
                                                    .collection("Estadisticas")
                                                    .document("PlantillaInicial")
                                                    .set(mapOf("mensaje" to "Subcolección creada automáticamente"))
                                                    .addOnSuccessListener {
                                                        Toast.makeText(this, "Registro exitoso y subcolección creada.", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, AuthActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this, "Error al crear subcolección: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Error al asociar paciente con médico: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al registrar paciente: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                // Guardar para otros roles
                                firestore.collection(collection).document(email)
                                    .set(additionalData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
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

    private fun loadDoctorsSpinner(spinner: Spinner) {
        firestore.collection("Medicos").get()
            .addOnSuccessListener { result ->
                val doctors = result.mapNotNull { it.id }
                updateSpinnerAdapter(spinner, doctors)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar médicos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPatientsSpinner(spinner: Spinner) {
        firestore.collection("Pacientes").get()
            .addOnSuccessListener { result ->
                val patients = result.mapNotNull { it.getString("name") }
                updateSpinnerAdapter(spinner, patients)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar pacientes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSpinnerAdapter(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}

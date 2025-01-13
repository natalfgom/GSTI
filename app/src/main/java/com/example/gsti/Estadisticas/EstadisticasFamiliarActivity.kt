package com.example.gsti.Estadisticas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gsti.InformacionPersonal.InformacionFamiliarActivity
import com.example.gsti.R
import com.example.gsti.SobreNosotros
import com.example.gsti.menuInicio.InicioFamiliar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EstadisticasFamiliarActivity : AppCompatActivity() {

    private lateinit var lineChartAtencion: LineChart
    private lateinit var lineChartMemoria: LineChart
    private lateinit var lineChartLenguaje: LineChart
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_paciente)
        Log.d("EstadisticasFamiliar", "Email del usuario actual: $currentUserEmail")


        // Inicializar los gráficos
        lineChartAtencion = findViewById(R.id.lineChartAtencion)
        lineChartMemoria = findViewById(R.id.lineChartMemoria)
        lineChartLenguaje = findViewById(R.id.lineChartLenguaje)

        val buttonVolverMenu: Button = findViewById(R.id.buttonVolverMenu)

        // Configuración del botón "Volver al Menú"
        buttonVolverMenu.setOnClickListener {
            val intent = Intent(this, InicioFamiliar::class.java)
            startActivity(intent)
            finish()
        }

        // Obtener el email del familiar autenticado
        if (currentUserEmail != null) {
            cargarPacienteAsociado(currentUserEmail)
        } else {
            Toast.makeText(this, "Error al obtener el email del familiar", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarPacienteAsociado(emailFamiliar: String) {
        Log.d("EstadisticasFamiliar", "Iniciando carga para email: $emailFamiliar")

        firestore.collection("Familiares")
            .document(emailFamiliar)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        Log.d("EstadisticasFamiliar", "Documento encontrado: ${document.data}")
                        val emailPaciente = document.getString("patient")
                        if (!emailPaciente.isNullOrEmpty()) {
                            Log.d("EstadisticasFamiliar", "Paciente asociado: $emailPaciente")
                            cargarDatosPaciente(emailPaciente)
                            cargarEstadisticas(emailPaciente)
                        } else {
                            showError("No hay paciente asociado a este familiar.")
                        }
                    } else {
                        Log.e("EstadisticasFamiliar", "Documento no encontrado para email: $emailFamiliar")
                        showError("Familiar no encontrado en la base de datos.")
                    }
                } else {
                    Log.e("EstadisticasFamiliar", "Error al realizar consulta: ${task.exception?.message}")
                    showError("Error al cargar paciente asociado: ${task.exception?.message}")
                }
            }

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun cargarDatosPaciente(emailPaciente: String) {
        firestore.collection("Pacientes")
            .document(emailPaciente)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        Log.d("EstadisticasFamiliar", "Datos del paciente: ${document.data}")
                        val nombre = document.getString("name") ?: "No disponible"
                        val apellido = document.getString("surname") ?: "No disponible"
                        val birthday = document.getString("birthday") ?: "No disponible"
                        val phone = document.getString("phone") ?: "No disponible"
                        val email = document.getString("email") ?: "No disponible"

                        // Asignar valores a los TextView
                        findViewById<TextView>(R.id.nombrePacienteTextView).text =
                            "$nombre $apellido"
                        findViewById<TextView>(R.id.birthdayPacienteTextView).text =
                            "Fecha de nacimiento: $birthday"
                        findViewById<TextView>(R.id.phonePacienteTextView).text =
                            "Teléfono: $phone"
                        findViewById<TextView>(R.id.emailPacienteTextView).text =
                            "Email: $email"
                    } else {
                        showError("Datos del paciente no encontrados.")
                    }
                } else {
                    showError("Error al cargar datos del paciente: ${task.exception?.message}")
                }
            }
    }

    private fun cargarEstadisticas(nombrePaciente: String) {
        setupLineChart(lineChartAtencion)
        setupLineChart(lineChartMemoria)
        setupLineChart(lineChartLenguaje)

        cargarDatosAtencion(nombrePaciente)
        cargarDatosMemoria(nombrePaciente)
        cargarDatosLenguaje(nombrePaciente)
    }

    private fun setupLineChart(lineChart: LineChart) {
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.setPinchZoom(true)
    }

    private fun cargarDatosAtencion(email: String) {
        firestore.collection("Pacientes")
            .document(email)
            .collection("Estadisticas")
            .document("Atencion")
            .collection("Partidas")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (!result.isEmpty) {
                        val entries = ArrayList<Entry>()
                        val dateLabels = ArrayList<String>()
                        var index = 0f

                        for (document in result.documents) {
                            val estrellas = document.getLong("estrellas")?.toFloat() ?: 0f
                            val fecha = document.getDate("fecha")

                            if (fecha != null) {
                                val formattedDate =
                                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(fecha)
                                dateLabels.add(formattedDate)
                                entries.add(Entry(index, estrellas))
                                index++
                            }
                        }

                        mostrarGrafico(lineChartAtencion, entries, dateLabels, "Progreso de Atención")
                    }
                }
            }
    }

    private fun cargarDatosMemoria(email: String) {
        firestore.collection("Pacientes")
            .document(email)
            .collection("Estadisticas")
            .document("Memoria")
            .collection("Partidas")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (!result.isEmpty) {
                        val entries = ArrayList<Entry>()
                        val dateLabels = ArrayList<String>()
                        var index = 0f

                        for (document in result.documents) {
                            val total = document.getLong("total")?.toFloat() ?: 0f
                            val fecha = document.getDate("fecha")

                            if (fecha != null) {
                                val formattedDate =
                                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(fecha)
                                dateLabels.add(formattedDate)
                                entries.add(Entry(index, total))
                                index++
                            }
                        }

                        mostrarGrafico(lineChartMemoria, entries, dateLabels, "Progreso de Memoria")
                    }
                }
            }
    }

    private fun cargarDatosLenguaje(email: String) {
        firestore.collection("Pacientes")
            .document(email)
            .collection("Estadisticas")
            .document("Lenguaje")
            .collection("Partidas")
            .get()
            .addOnSuccessListener { result ->
                val entries = ArrayList<Entry>()
                val dateLabels = ArrayList<String>()

                var index = 0f
                for (document in result) {
                    val total = document.getLong("total")?.toFloat() ?: 0f
                    val fecha = document.getDate("fecha")

                    if (fecha != null) {
                        val formattedDate = SimpleDateFormat("dd/MM", Locale.getDefault()).format(fecha)
                        dateLabels.add(formattedDate)
                        entries.add(Entry(index, total))
                        index++
                    }
                }

                if (entries.isNotEmpty()) {
                    mostrarGrafico(lineChartLenguaje, entries, dateLabels, "Progreso de Lenguaje")
                }
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun mostrarGrafico(
        lineChart: LineChart,
        entries: ArrayList<Entry>,
        dateLabels: ArrayList<String>,
        label: String
    ) {
        val lineDataSet = LineDataSet(entries, label)
        lineDataSet.color = resources.getColor(R.color.blue, null)
        lineDataSet.valueTextColor = resources.getColor(R.color.black, null)
        lineDataSet.circleColors = listOf(resources.getColor(R.color.blue, null))
        lineDataSet.lineWidth = 2f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        lineChart.invalidate()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("EstadisticasFamiliar", message)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                val intent = Intent(this, InicioFamiliar::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_informacion_personal -> {
                val intent = Intent(this, InformacionFamiliarActivity::class.java)
                startActivity(intent)
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
}

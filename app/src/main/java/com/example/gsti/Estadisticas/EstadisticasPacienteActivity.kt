package com.example.gsti.Estadisticas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioMedico
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EstadisticasPacienteActivity : AppCompatActivity() {

    private lateinit var lineChartAtencion: LineChart
    private lateinit var lineChartMemoria: LineChart
    private lateinit var lineChartLenguaje: LineChart
    private lateinit var nombrePacienteTextView: TextView
    private lateinit var birthdayPacienteTextView: TextView
    private lateinit var phonePacienteTextView: TextView
    private lateinit var emailPacienteTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_paciente)

        // Inicializar los elementos de la UI
        lineChartAtencion = findViewById(R.id.lineChartAtencion)
        lineChartMemoria = findViewById(R.id.lineChartMemoria)
        lineChartLenguaje = findViewById(R.id.lineChartLenguaje)
        nombrePacienteTextView = findViewById(R.id.nombrePacienteTextView)
        birthdayPacienteTextView = findViewById(R.id.birthdayPacienteTextView)
        phonePacienteTextView = findViewById(R.id.phonePacienteTextView)
        emailPacienteTextView = findViewById(R.id.emailPacienteTextView)

        val buttonVolverMenu: Button = findViewById(R.id.buttonVolverMenu)

        // Configuración del botón "Volver al Menú"
        buttonVolverMenu.setOnClickListener {
            val intent = Intent(this, InicioMedico::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad
        }

        // Obtener el email del paciente desde el intent
        val emailPaciente = intent.getStringExtra("EMAIL_PACIENTE")
        if (emailPaciente != null) {
            cargarDatosPersonales(emailPaciente)
            cargarEstadisticas(emailPaciente)
        } else {
            Toast.makeText(this, "No se pudo obtener el email del paciente", Toast.LENGTH_SHORT).show()
            finish() // Finaliza la actividad si no hay un email válido
        }
    }

    private fun setupLineChart(lineChart: LineChart) {
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.setPinchZoom(true)
    }

    private fun cargarDatosPersonales(emailPaciente: String) {
        firestore.collection("Pacientes").document(emailPaciente).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("name") ?: "Desconocido"
                    val birthday = document.getString("birthday") ?: "No especificado"
                    val phone = document.getString("phone") ?: "No especificado"
                    val email = document.getString("email") ?: "No especificado"

                    // Mostrar los datos personales en los TextViews
                    nombrePacienteTextView.text = nombre
                    birthdayPacienteTextView.text = "Fecha de Nacimiento: $birthday"
                    phonePacienteTextView.text = "Teléfono: $phone"
                    emailPacienteTextView.text = "Email: $email"
                } else {
                    Toast.makeText(this, "No se encontraron datos del paciente.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos personales: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarEstadisticas(emailPaciente: String) {
        // Configurar gráficos
        setupLineChart(lineChartAtencion)
        setupLineChart(lineChartMemoria)
        setupLineChart(lineChartLenguaje)

        // Cargar estadísticas de Atención
        cargarDatosAtencion(emailPaciente)

        // Cargar estadísticas de Memoria
        cargarDatosMemoria(emailPaciente)

        // Cargar estadísticas de Lenguaje
        cargarDatosLenguaje(emailPaciente)
    }

    private fun cargarDatosAtencion(email: String) {
        firestore.collection("Pacientes")
            .document(email)
            .collection("Estadisticas")
            .document("Atencion")
            .collection("Partidas")
            .get()
            .addOnSuccessListener { result ->
                val entries = ArrayList<Entry>()
                val dateLabels = ArrayList<String>()

                var index = 0f
                for (document in result) {
                    val estrellas = document.getLong("estrellas")?.toFloat() ?: 0f
                    val fecha = document.getDate("fecha")

                    if (fecha != null) {
                        val formattedDate = SimpleDateFormat("dd/MM", Locale.getDefault()).format(fecha)
                        dateLabels.add(formattedDate)
                        entries.add(Entry(index, estrellas))
                        index++
                    }
                }

                if (entries.isNotEmpty()) {
                    mostrarGrafico(lineChartAtencion, entries, dateLabels, "Progreso de Atención")
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
                    mostrarGrafico(lineChartMemoria, entries, dateLabels, "Progreso de Memoria")
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

        // Configurar etiquetas en el eje X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        lineChart.invalidate()
    }
}

package com.example.gsti.Estadisticas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.example.gsti.menuInicio.InicioFamiliar
import com.example.gsti.menuInicio.InicioPaciente
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_paciente)

        // Inicializar los gráficos
        lineChartAtencion = findViewById(R.id.lineChartAtencion)
        lineChartMemoria = findViewById(R.id.lineChartMemoria)
        lineChartLenguaje = findViewById(R.id.lineChartLenguaje)

        val buttonVolverMenu: Button = findViewById(R.id.buttonVolverMenu)

        // Configuración del botón "Volver al Menú"
        buttonVolverMenu.setOnClickListener {
            val intent = Intent(this, InicioFamiliar::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad
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
                        val nombrePaciente = document.getString("patient")
                        if (!nombrePaciente.isNullOrEmpty()) {
                            Log.d("EstadisticasFamiliar", "Paciente asociado: $nombrePaciente")
                            cargarEstadisticas(nombrePaciente)
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
    }


    private fun setupLineChart(lineChart: LineChart) {
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.setPinchZoom(true)
    }

    private fun cargarEstadisticas(nombrePaciente: String) {
        // Configurar gráficos
        setupLineChart(lineChartAtencion)
        setupLineChart(lineChartMemoria)
        setupLineChart(lineChartLenguaje)

        // Cargar estadísticas de Atención
        cargarDatosAtencion(nombrePaciente)

        // Cargar estadísticas de Memoria
        cargarDatosMemoria(nombrePaciente)

        // Cargar estadísticas de Lenguaje
        cargarDatosLenguaje(nombrePaciente)
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
                    } else {
                        Log.d("EstadisticasFamiliar", "No hay datos de Atención para $email")
                    }
                } else {
                    showError("Error al cargar datos de Atención: ${task.exception?.message}")
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
                    } else {
                        Log.d("EstadisticasFamiliar", "No hay datos de Memoria para $email")
                    }
                } else {
                    showError("Error al cargar datos de Memoria: ${task.exception?.message}")
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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (!result.isEmpty) {
                        val entries = ArrayList<Entry>()
                        val dateLabels = ArrayList<String>()
                        var index = 0f

                        for (document in result.documents) {
                            val puntuacion = document.getLong("puntuacion")?.toFloat() ?: 0f
                            val fecha = document.getDate("fecha")

                            if (fecha != null) {
                                val formattedDate =
                                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(fecha)
                                dateLabels.add(formattedDate)
                                entries.add(Entry(index, puntuacion))
                                index++
                            }
                        }

                        mostrarGrafico(lineChartLenguaje, entries, dateLabels, "Progreso de Lenguaje")
                    } else {
                        Log.d("EstadisticasFamiliar", "No hay datos de Lenguaje para $email")
                    }
                } else {
                    showError("Error al cargar datos de Lenguaje: ${task.exception?.message}")
                }
            }
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

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("EstadisticasFamiliar", message)
        finish()
    }
}

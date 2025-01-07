package com.example.gsti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        // Inicializar el gráfico
        lineChart = findViewById(R.id.lineChart)
        setupLineChart()

        // Cargar datos desde Firestore
        if (currentUserEmail != null) {
            cargarDatosEstadisticas(currentUserEmail)
        }
    }

    private fun setupLineChart() {
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.axisRight.isEnabled = false
        lineChart.setPinchZoom(true)
    }

    private fun cargarDatosEstadisticas(email: String) {
        firestore.collection("Pacientes")
            .document(email)
            .collection("Estadisticas")
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
                    mostrarGrafico(entries, dateLabels)
                }
            }
            .addOnFailureListener { e ->
                // Manejo de errores al cargar los datos
                e.printStackTrace()
            }
    }

    private fun mostrarGrafico(entries: ArrayList<Entry>, dateLabels: ArrayList<String>) {
        val lineDataSet = LineDataSet(entries, "Progreso de estrellas")
        lineDataSet.color = resources.getColor(R.color.blue, null)
        lineDataSet.valueTextColor = resources.getColor(R.color.black, null)
        lineDataSet.circleColors = listOf(resources.getColor(R.color.blue, null))
        lineDataSet.lineWidth = 2f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Configurar etiquetas en el eje X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        lineChart.invalidate() // Actualizar el gráfico
    }
}

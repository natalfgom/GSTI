package com.example.gsti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.InformacionPersonal.InformacionPacienteActivity
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

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var sectionAtencion: LinearLayout
    private lateinit var sectionMemoria: LinearLayout
    private lateinit var sectionLenguaje: LinearLayout

    private lateinit var lineChartAtencion: LineChart
    private lateinit var lineChartMemoria: LineChart
    private lateinit var lineChartLenguaje: LineChart
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)



        // Inicializar secciones y gráficos
        sectionAtencion = findViewById(R.id.sectionAtencion)
        sectionMemoria = findViewById(R.id.sectionMemoria)
        sectionLenguaje = findViewById(R.id.sectionLenguaje)
        lineChartAtencion = findViewById(R.id.lineChartAtencion)
        lineChartMemoria = findViewById(R.id.lineChartMemoria)
        lineChartLenguaje = findViewById(R.id.lineChartLenguaje)
        val buttonVolverMenu: Button = findViewById(R.id.buttonVolverMenu)

        // Configuración del botón "Volver al Menú"
        buttonVolverMenu.setOnClickListener {
            val intent = Intent(this, InicioPaciente::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad
        }

        // Configuración de los gráficos...
        setupLineChart(lineChartAtencion)
        setupLineChart(lineChartMemoria)
        setupLineChart(lineChartLenguaje)

        // Cargar datos Firestore
        if (currentUserEmail != null) {
            firestore.collection("Pacientes").document(currentUserEmail)
                .get()
                .addOnSuccessListener { document ->
                    val juegoAtencionActivo = document.getBoolean("juegoAtencionActivo") ?: false
                    val juegoMemoriaActivo = document.getBoolean("juegoMemoriaActivo") ?: false
                    val juegoLenguajeActivo = document.getBoolean("juegoLenguajeActivo") ?: false

                    // Configurar visibilidad de secciones
                    sectionAtencion.visibility = if (juegoAtencionActivo) LinearLayout.VISIBLE else LinearLayout.GONE
                    sectionMemoria.visibility = if (juegoMemoriaActivo) LinearLayout.VISIBLE else LinearLayout.GONE
                    sectionLenguaje.visibility = if (juegoLenguajeActivo) LinearLayout.VISIBLE else LinearLayout.GONE

                    // Cargar datos de los gráficos si están activos
                    if (juegoAtencionActivo) cargarDatosAtencion(currentUserEmail)
                    if (juegoMemoriaActivo) cargarDatosMemoria(currentUserEmail)
                    if (juegoLenguajeActivo) cargarDatosLenguaje(currentUserEmail)
                }
                .addOnFailureListener { e -> e.printStackTrace() }
        }

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

    }
    // Acciones al seleccionar un ítem del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_principal -> {
                val intent = Intent(this, InicioPaciente::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_informacion_personal -> {
                // Redirigir a la actividad de información personal
                val intent = Intent(this, InformacionPacienteActivity::class.java)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu) // Asegúrate de que `menu_toolbar` existe
        return true
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
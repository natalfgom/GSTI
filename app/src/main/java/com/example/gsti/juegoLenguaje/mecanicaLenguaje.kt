package com.example.gsti.juegoLenguaje

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MecanicaLenguaje : AppCompatActivity() {

    private lateinit var textViewPalabra: TextView
    private lateinit var textViewTimer: TextView
    private lateinit var buttonCategoria1: Button
    private lateinit var buttonCategoria2: Button
    private lateinit var buttonCategoria3: Button

    private var palabra: String = ""
    private var categoriaCorrecta: String = "" // Categoría correcta asociada a la palabra
    private var aciertos: Int = 0
    private var rondasRestantes: Int = 5

    // Variables para el temporizador
    private lateinit var countDownTimer: CountDownTimer
    private val totalTimeInMillis: Long = 50000 // 1 minuto y 30 segundos (90,000 ms)
    private var timeLeftInMillis: Long = totalTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego_lenguaje)

        // Asociamos las vistas con los elementos del layout
        textViewPalabra = findViewById(R.id.textViewPalabra)
        textViewTimer = findViewById(R.id.timerTextView)
        buttonCategoria1 = findViewById(R.id.buttonCategoria1)
        buttonCategoria2 = findViewById(R.id.buttonCategoria2)
        buttonCategoria3 = findViewById(R.id.buttonCategoria3)

        // Configuramos las categorías en los botones
        buttonCategoria1.setOnClickListener { verificarCategoria(buttonCategoria1.text.toString(), buttonCategoria1) }
        buttonCategoria2.setOnClickListener { verificarCategoria(buttonCategoria2.text.toString(), buttonCategoria2) }
        buttonCategoria3.setOnClickListener { verificarCategoria(buttonCategoria3.text.toString(), buttonCategoria3) }

        // Iniciamos el temporizador global
        startGlobalTimer()

        // Iniciamos la primera ronda
        iniciarRonda()
    }
    private fun guardarEstadisticasFinales(totalScore: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val pacienteId = FirebaseAuth.getInstance().currentUser?.email // ID del paciente actual
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date()) // Formato de fecha

        if (pacienteId != null) {
            val estadisticasFinales = hashMapOf(
                "total" to totalScore,
                "fecha" to Timestamp.now()
            )

            firestore.collection("Pacientes")
                .document(pacienteId)
                .collection("Estadisticas")
                .document("Lenguaje") // Subcolección "Lenguaje"
                .collection("Partidas") // Subcolección "Partidas"
                .document(fecha) // Documento con la fecha como ID
                .set(estadisticasFinales)
                .addOnSuccessListener {
                    Toast.makeText(this, "Estadísticas guardadas correctamente.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar estadísticas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    // Función para iniciar una nueva ronda
    private fun iniciarRonda() {
        if (rondasRestantes > 0) {
            generarPalabra()
            resetearBotones() // Reiniciar los botones al inicio de cada ronda
            rondasRestantes--
        } else {
            finalizarJuego()
        }
    }

    // Función para reiniciar los botones (color gris) al inicio de cada ronda
    private fun resetearBotones() {
        buttonCategoria1.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        buttonCategoria2.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        buttonCategoria3.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }

    // Función para generar una palabra aleatoria y asignar la categoría correcta
    private fun generarPalabra() {
        val palabrasMuebles = listOf("Mesa", "Estanteria", "Armario", "Silla", "Cama")
        val palabrasAnimales = listOf("Perro", "Gato", "Caballo", "Raton", "Mariposa")
        val palabrasObjetos = listOf("Tenedor", "Libro", "Bolígrafo", "Llaves", "Bolso")

        // Elegimos aleatoriamente una categoría
        val categoriaAleatoria = (1..3).random()
        when (categoriaAleatoria) {
            1 -> {
                palabra = palabrasMuebles.random()
                categoriaCorrecta = "Muebles"
            }
            2 -> {
                palabra = palabrasAnimales.random()
                categoriaCorrecta = "Animales"
            }
            3 -> {
                palabra = palabrasObjetos.random()
                categoriaCorrecta = "Objetos"
            }
        }

        textViewPalabra.text = palabra // Mostramos la palabra en el TextView

        // Asignamos las categorías a los botones asegurándonos de que la categoría correcta esté siempre presente
        val categorias = mutableListOf("Muebles", "Animales", "Objetos")
        categorias.remove(categoriaCorrecta) // Eliminamos la categoría correcta para asignarla manualmente

        // Mezclamos las categorías incorrectas
        categorias.shuffle()

        // Asignamos la categoría correcta a un botón aleatorio y las demás a los otros botones
        val posicionCorrecta = (1..3).random()
        when (posicionCorrecta) {
            1 -> {
                buttonCategoria1.text = categoriaCorrecta
                buttonCategoria2.text = categorias[0]
                buttonCategoria3.text = categorias[1]
            }
            2 -> {
                buttonCategoria2.text = categoriaCorrecta
                buttonCategoria1.text = categorias[0]
                buttonCategoria3.text = categorias[1]
            }
            3 -> {
                buttonCategoria3.text = categoriaCorrecta
                buttonCategoria1.text = categorias[0]
                buttonCategoria2.text = categorias[1]
            }
        }
    }

    // Función para verificar si la categoría seleccionada es correcta
    private fun verificarCategoria(categoriaSeleccionada: String, botonSeleccionado: Button) {
        if (categoriaSeleccionada == categoriaCorrecta) {
            botonSeleccionado.setBackgroundColor(resources.getColor(android.R.color.holo_green_light)) // Correcto
            aciertos++
        } else {
            botonSeleccionado.setBackgroundColor(resources.getColor(android.R.color.holo_red_light)) // Incorrecto
        }

        // Esperar un breve momento para mostrar la respuesta antes de pasar a la siguiente ronda
        android.os.Handler().postDelayed({ iniciarRonda() }, 1000)
    }

    // Función para iniciar el temporizador global
    private fun startGlobalTimer() {
        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                // El tiempo se agotó
                Toast.makeText(this@MecanicaLenguaje, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                finalizarJuego()
            }
        }
        countDownTimer.start()
    }

    // Función para actualizar la UI del temporizador
    private fun updateTimerUI() {
        val secondsLeft = (timeLeftInMillis / 1000).toInt()
        textViewTimer.text = "Tiempo restante: ${secondsLeft}s"
    }

    // Función para finalizar el juego
    private fun finalizarJuego() {
        // Cancelamos el temporizador
        countDownTimer.cancel()

        // Guardar estadísticas finales
        guardarEstadisticasFinales(aciertos)

        // Verificar si el jugador ha ganado o perdido
        if (aciertos >= 3) {
            // Ir a la pantalla de éxito
            val intent = Intent(this, FelicitacionActivity::class.java)
            intent.putExtra("ACERTADOS", aciertos) // Enviar el número de aciertos
            startActivity(intent)
        } else {
            // Ir a la pantalla de fallo
            val intent = Intent(this, IntentarDeNuevoActivity::class.java)
            intent.putExtra("ACERTADOS", aciertos) // Enviar el número de aciertos
            startActivity(intent)
        }
        finish() // Finalizar la actividad actual
    }





    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel() // Cancelamos el temporizador al destruir la actividad
    }
}

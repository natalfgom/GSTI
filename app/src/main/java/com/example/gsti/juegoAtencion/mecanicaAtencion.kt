package com.example.gsti.juegoAtencion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MecanicaAtencion : AppCompatActivity() {

    private lateinit var numberGrid: GridLayout
    private lateinit var targetNumberText: TextView
    private lateinit var timerText: TextView

    private var targetNumber: Int = 0
    private var correctTaps: Int = 0
    private var totalTargetCount: Int = 0
    private var remainingTouches: Int = 5
    private val timeLimit = 50000L
    private var gameRunning = false
    private var currentLevel = 1
    private var totalStars = 3
    private var failedLevels = 0

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atencion)

        numberGrid = findViewById(R.id.numberGrid)
        targetNumberText = findViewById(R.id.targetNumberText)
        timerText = findViewById(R.id.timerText)

        startGame()
    }

    private fun startGame() {
        gameRunning = true
        correctTaps = 0
        totalTargetCount = 0
        remainingTouches = 5

        timer?.cancel()

        targetNumber = (1..10).random()
        targetNumberText.text = "Selecciona el número: $targetNumber"

        numberGrid.removeAllViews()
        val positions = (0 until 12).shuffled()
        val targetPositions = positions.take(3)

        for (i in 0 until 12) {
            val button = Button(this)
            val number = if (i in targetPositions) {
                targetNumber
            } else {
                (1..10).filter { it != targetNumber }.random()
            }

            if (number == targetNumber) {
                totalTargetCount++
            }

            button.text = number.toString()
            button.textSize = 20f
            button.setBackgroundColor(Color.LTGRAY)

            button.setOnClickListener {
                if (gameRunning) {
                    if (button.text.toString().toInt() == targetNumber) {
                        correctTaps++
                        button.setBackgroundColor(Color.GREEN)
                        button.isEnabled = false
                    } else {
                        button.setBackgroundColor(Color.RED)
                        button.isEnabled = false
                    }

                    remainingTouches--

                    if (correctTaps == totalTargetCount || remainingTouches == 0) {
                        gameRunning = false
                        handleLevelCompletion()
                    }
                }
            }

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            button.layoutParams = params

            numberGrid.addView(button)
        }

        timer = object : CountDownTimer(timeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Tiempo restante: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                if (gameRunning) {
                    gameRunning = false
                    handleLevelCompletion()
                }
            }
        }.start()
    }

    private fun handleLevelCompletion() {
        if (correctTaps < totalTargetCount) {
            failedLevels++ // Incrementa el contador de niveles fallidos si no se seleccionaron todos los números objetivos
        }

        if (currentLevel < 3) {
            currentLevel++
            startGame()
        } else {
            calculateStarsAndSaveResult()
        }
    }

    private fun calculateStarsAndSaveResult() {
        // Calcular estrellas basándose en niveles fallidos
        totalStars = when (failedLevels) {
            0 -> 3
            1 -> 2
            else -> 1
        }

        // Guardar estadísticas finales en Firestore
        guardarEstadisticasFinales(totalStars)
    }

    private fun guardarEstadisticasFinales(estrellas: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val pacienteId = FirebaseAuth.getInstance().currentUser?.email // ID del paciente actual
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (pacienteId != null) {
            val estadisticasFinales = hashMapOf(
                "estrellas" to estrellas,
                "fecha" to Timestamp.now()
            )

            firestore.collection("Pacientes")
                .document(pacienteId)
                .collection("Estadisticas")
                .document(fecha) // Guardar por fecha
                .set(estadisticasFinales)
                .addOnSuccessListener {
                    Toast.makeText(this, "Estadísticas guardadas correctamente.", Toast.LENGTH_SHORT).show()
                    // Mostrar pantalla de resultados
                    val intent = Intent(this, SuccessActivity::class.java)
                    intent.putExtra("stars", estrellas)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar estadísticas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

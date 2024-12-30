package com.example.gsti.juegoAtencion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class MecanicaAtencion : AppCompatActivity() {
    private lateinit var numberGrid: GridLayout
    private lateinit var targetNumberText: TextView
    private lateinit var timerText: TextView

    private var targetNumber: Int = 0 // Número objetivo
    private var correctTaps: Int = 0 // Número de selecciones correctas
    private var totalTargetCount: Int = 0 // Total de números objetivo en la tabla
    private var remainingTouches: Int = 5 // Máximo número de toques permitidos
    private val timeLimit = 50000L // Tiempo límite en milisegundos (50 segundos)
    private var gameRunning = false
    private var currentLevel = 1 // Nivel actual (1 a 3)
    private var totalStars = 0 // Número total de estrellas obtenidas

    private var timer: CountDownTimer? = null // Variable para manejar el temporizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atencion)

        // Inicializar vistas
        numberGrid = findViewById(R.id.numberGrid)
        targetNumberText = findViewById(R.id.targetNumberText)
        timerText = findViewById(R.id.timerText)

        // Iniciar el primer nivel del juego
        startGame()
    }

    private fun startGame() {
        gameRunning = true
        correctTaps = 0
        totalTargetCount = 0
        remainingTouches = 5 // Reiniciar el número de toques permitidos

        // Cancelar el temporizador anterior si existe
        timer?.cancel()

        // Generar el número objetivo
        targetNumber = (1..10).random()
        targetNumberText.text = "Selecciona el número: $targetNumber"

        // Llenar la cuadrícula con números aleatorios asegurando que el número objetivo esté al menos 3 veces
        numberGrid.removeAllViews()
        val positions = (0 until 12).shuffled() // Generar posiciones aleatorias para los botones
        val targetPositions = positions.take(3) // Seleccionar 3 posiciones para el número objetivo

        for (i in 0 until 12) {
            val button = Button(this)
            val number = if (i in targetPositions) {
                targetNumber // Asegurar que el número objetivo esté en estas posiciones
            } else {
                (1..10).filter { it != targetNumber }.random() // Evitar duplicar el número objetivo en otras posiciones
            }

            if (number == targetNumber) {
                totalTargetCount++ // Incrementar el contador si es el número objetivo
            }

            button.text = number.toString()
            button.textSize = 20f
            button.setBackgroundColor(Color.LTGRAY)

            // Asegurar que el botón responda a las selecciones
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

                    // Finalizar el nivel si se alcanzan 5 toques o si se seleccionan todos los objetivos
                    if (correctTaps == totalTargetCount || remainingTouches == 0) {
                        gameRunning = false
                        handleLevelCompletion()
                    }
                }
            }

            // Configurar las dimensiones del botón en el GridLayout
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8) // Márgenes uniformes
            button.layoutParams = params

            numberGrid.addView(button)
        }

        // Iniciar el temporizador
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
        // Determinar si el jugador logró un 100% de aciertos en este nivel
        val accuracy = (correctTaps * 100) / totalTargetCount
        if (accuracy == 100) {
            totalStars++ // Incrementar estrellas por cada nivel perfecto
        }

        if (currentLevel < 3) {
            currentLevel++
            startGame() // Ir al siguiente nivel
        } else {
            // Evaluar el resultado final después de 3 niveles
            calculateStarsAndShowResult()
        }
    }

    private fun calculateStarsAndShowResult() {
        // Mostrar la pantalla de éxito con el número de estrellas
        val intent = Intent(this, SuccessActivity::class.java)
        intent.putExtra("stars", totalStars) // Pasar las estrellas obtenidas
        startActivity(intent)
        finish()
    }

    private fun showFailureScreen() {
        val intent = Intent(this, FailureActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelar el temporizador si la actividad se destruye
        timer?.cancel()
    }
}

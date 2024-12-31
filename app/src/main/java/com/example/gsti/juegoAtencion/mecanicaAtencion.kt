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

    // Declaración de vistas y variables necesarias
    private lateinit var numberGrid: GridLayout // Contenedor para los botones con números
    private lateinit var targetNumberText: TextView // Texto que muestra el número objetivo
    private lateinit var timerText: TextView // Texto que muestra el tiempo restante

    private var targetNumber: Int = 0 // Número objetivo que el jugador debe seleccionar
    private var correctTaps: Int = 0 // Número de selecciones correctas realizadas por el jugador
    private var totalTargetCount: Int = 0 // Número total de veces que el número objetivo aparece en la cuadrícula
    private var remainingTouches: Int = 5 // Toques restantes permitidos
    private val timeLimit = 50000L // Tiempo límite por nivel en milisegundos (50 segundos)
    private var gameRunning = false // Indica si el juego está en ejecución
    private var currentLevel = 1 // Nivel actual del juego (hay 3 niveles)
    private var totalStars = 0 // Total de estrellas obtenidas al final del juego

    private var timer: CountDownTimer? = null // Temporizador que controla el tiempo de cada nivel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atencion)

        // Vinculación de vistas
        numberGrid = findViewById(R.id.numberGrid)
        targetNumberText = findViewById(R.id.targetNumberText)
        timerText = findViewById(R.id.timerText)

        // Iniciar el juego desde el primer nivel
        startGame()
    }

    // Función para configurar y empezar cada nivel
    private fun startGame() {
        gameRunning = true
        correctTaps = 0 // Reinicia las selecciones correctas al comenzar un nuevo nivel
        totalTargetCount = 0 // Reinicia el contador de números objetivos
        remainingTouches = 5 // Reinicia los toques permitidos

        // Cancelar cualquier temporizador previo antes de configurar uno nuevo
        timer?.cancel()

        // Generar un número objetivo aleatorio del 1 al 10
        targetNumber = (1..10).random()
        targetNumberText.text = "Selecciona el número: $targetNumber"

        // Configuración de la cuadrícula con botones numéricos
        numberGrid.removeAllViews() // Limpia cualquier vista previa de la cuadrícula
        val positions = (0 until 12).shuffled() // Genera una lista de posiciones aleatorias
        val targetPositions = positions.take(3) // Selecciona 3 posiciones para el número objetivo

        // Agregar botones a la cuadrícula
        for (i in 0 until 12) {
            val button = Button(this)
            val number = if (i in targetPositions) {
                targetNumber // Asegura que el número objetivo aparezca en las posiciones seleccionadas
            } else {
                (1..10).filter { it != targetNumber }.random() // Evita que se repita el número objetivo en posiciones no seleccionadas
            }

            // Contador de cuántas veces aparece el número objetivo
            if (number == targetNumber) {
                totalTargetCount++
            }

            button.text = number.toString() // Muestra el número en el botón
            button.textSize = 20f // Tamaño del texto del botón
            button.setBackgroundColor(Color.LTGRAY) // Color inicial del botón

            // Configuración del clic del botón
            button.setOnClickListener {
                if (gameRunning) {
                    if (button.text.toString().toInt() == targetNumber) {
                        correctTaps++ // Incrementa las selecciones correctas
                        button.setBackgroundColor(Color.GREEN) // Cambia el color a verde si es correcto
                        button.isEnabled = false // Desactiva el botón después de seleccionarlo
                    } else {
                        button.setBackgroundColor(Color.RED) // Cambia el color a rojo si es incorrecto
                        button.isEnabled = false // Desactiva el botón después de seleccionarlo
                    }

                    remainingTouches-- // Reduce el número de toques restantes

                    // Verifica si el nivel se completa (aciertos suficientes o se acabaron los toques)
                    if (correctTaps == totalTargetCount || remainingTouches == 0) {
                        gameRunning = false
                        handleLevelCompletion() // Maneja el final del nivel
                    }
                }
            }

            // Configuración del diseño del botón en la cuadrícula
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8) // Establece márgenes uniformes
            button.layoutParams = params

            numberGrid.addView(button) // Añade el botón a la cuadrícula
        }

        // Configuración del temporizador para el nivel
        timer = object : CountDownTimer(timeLimit, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Actualiza el texto del temporizador
                timerText.text = "Tiempo restante: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                // Si el tiempo termina y el juego sigue activo, se maneja el final del nivel
                if (gameRunning) {
                    gameRunning = false
                    handleLevelCompletion()
                }
            }
        }.start()
    }

    // Maneja lo que sucede al finalizar un nivel
    private fun handleLevelCompletion() {
        val accuracy = (correctTaps * 100) / totalTargetCount // Calcula la precisión del jugador
        if (accuracy == 100) {
            totalStars++ // Incrementa las estrellas si el nivel fue completado perfectamente
        }

        if (currentLevel < 3) {
            // Si no es el último nivel, pasa al siguiente nivel
            currentLevel++
            startGame()
        } else {
            // Si es el último nivel, calcula el resultado final
            calculateStarsAndShowResult()
        }
    }

    // Calcula las estrellas obtenidas y muestra la pantalla de resultado
    private fun calculateStarsAndShowResult() {
        if (totalStars == 0) {
            // Si no se obtuvo ninguna estrella, muestra la pantalla de fallo
            showFailureScreen()
        } else {
            // Si se obtuvo al menos una estrella, muestra la pantalla de éxito
            val intent = Intent(this, SuccessActivity::class.java)
            intent.putExtra("stars", totalStars) // Pasa las estrellas obtenidas
            startActivity(intent)
            finish()
        }
    }

    // Muestra la pantalla de fallo
    private fun showFailureScreen() {
        val intent = Intent(this, FailureActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Cancela el temporizador cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

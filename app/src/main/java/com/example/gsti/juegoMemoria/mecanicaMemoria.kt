package com.example.gsti.juegoMemoria

import android.widget.LinearLayout
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gsti.R

class MecanicaMemoria : AppCompatActivity() {

    // Declaración de vistas y variables necesarias
    private lateinit var wordGrid: GridLayout // Contenedor para las palabras
    private lateinit var wordText: TextView // Texto que muestra las palabras
    private lateinit var timerText: TextView // Texto que muestra el tiempo restante
    private lateinit var progressBar: ProgressBar

    private val lists = listOf(
        listOf("silla", "azul", "arena", "gorro", "gato"),
        listOf("puerta", "lazo", "manzana", "casa", "libro"),
        listOf("precio", "guante", "perro", "fuente", "aula")
    )

    private var currentList: List<String> = lists.random() // Selección aleatoria de una lista
    private var currentRound = 1 // Ronda actual (cada lista se juega 3 veces)
    private var selectedWords = mutableListOf<String>() // Palabras seleccionadas por el jugador
    private var displayedWords = mutableListOf<String>() // Palabras mostradas en la cuadrícula

    private var timer: CountDownTimer? = null // Temporizador que controla el tiempo

    // Variable para guardar las puntuaciones de cada ronda
    private val roundScores = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.juego_memoria)

        // Vinculación de vistas
        wordGrid = findViewById(R.id.wordGrid)
        timerText = findViewById(R.id.timerText)

        // Iniciar el juego con la lista aleatoria
        startGame()
    }

    // Configura y empieza la ronda actual
    private fun startGame() {
        if (currentRound > 3) {
            // Si se completaron las 3 rondas, mostrar la pantalla de puntuaciones finales
            showFinalScores()
            return
        }

        // Mostrar las palabras de la lista seleccionada
        showWords(currentList)
    }

    private fun showWords(list: List<String>) {
        var index = 0

        // Referenciar el LinearLayout donde agregarás el ImageView y TextView
        val wordContainer = findViewById<LinearLayout>(R.id.wordContainer)
        val wordGrid = findViewById<GridLayout>(R.id.wordGrid) // Cuadrícula que debe ocultarse temporalmente

        // Ocultar la cuadrícula antes de mostrar las palabras
        wordGrid.visibility = GridLayout.GONE

        // Ocultar el temporizador de tiempo restante mientras se muestran las palabras
        timerText.visibility = TextView.GONE

        // Crear el ImageView dinámicamente
        val imageView = ImageView(this) // Crea un nuevo ImageView
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,  // Usar un tamaño fijo proporcional
            700 // Altura fija,
        ).apply {
            setMargins(16, 16, 16, 16) // Márgenes alrededor de la imagen
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE // Ajusta la imagen dentro del marco sin recortar
        imageView.adjustViewBounds = true // Permite que las dimensiones se ajusten automáticamente
        imageView.setBackgroundColor(Color.TRANSPARENT) // Establece el fondo como transparente

        // Crear el TextView dinámicamente para mostrar las palabras
        val wordTextView = TextView(this)
        wordTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        wordTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        wordTextView.textSize = 60f
        wordTextView.setTextColor(Color.parseColor("#744CAF"))

        // Añadir el ImageView y TextView al contenedor
        wordContainer.removeAllViews()  // Limpiar el contenedor antes de agregar nuevos elementos
        wordContainer.addView(imageView)
        wordContainer.addView(wordTextView)

        // Temporizador para mostrar las palabras una a una con su imagen correspondiente
        timer = object : CountDownTimer(list.size * 2000L, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                if (index < list.size) {
                    val word = list[index].replaceFirstChar { it.uppercase() } // Capitaliza la primera letra
                    val word2 = list[index]
                    wordTextView.text = word  // Mostrar la palabra en el TextView

                    // Obtener la imagen correspondiente a la palabra desde drawable
                    val imageResId = resources.getIdentifier(word2, "drawable", packageName)

                    // Verificar si la imagen existe
                    if (imageResId != 0) {
                        imageView.setImageResource(imageResId) // Mostrar la imagen correspondiente
                    } else {
                        imageView.setImageResource(R.drawable.sobre_nosotros) // Imagen por defecto si no existe la imagen
                    }

                    index++
                }
            }

            override fun onFinish() {
                wordTextView.text = ""  // Limpiar el texto
                imageView.setImageResource(0)  // Limpiar la imagen

                // Mostrar la cuadrícula después de haber terminado de mostrar las palabras
                wordGrid.visibility = GridLayout.VISIBLE

                // Volver a mostrar el temporizador de tiempo restante
                timerText.visibility = TextView.VISIBLE

                setupWordSelection(list)
            }
        }.start()
    }

    private fun setupWordSelection(list: List<String>) {
        displayedWords.clear()
        selectedWords.clear()

        // Mezclar palabras de la lista con palabras de distracción
        val distractorWords = lists.flatten().shuffled().filterNot { it in list }.take(10)
        displayedWords.addAll((list + distractorWords).shuffled())

        wordGrid.removeAllViews()

        // Ajuste del número de columnas y márgenes para un mejor diseño
        val columnCount = 3 // Número de columnas para el GridLayout
        wordGrid.columnCount = columnCount

        displayedWords.forEach { word ->
            // Crear el contenedor para la imagen y el nombre de la palabra
            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // Distribuir el espacio
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // Hacer las celdas más grandes
                }
                setPadding(16, 16, 16, 16) // Márgenes internos
                gravity = Gravity.CENTER

                // Fondo con borde alrededor del cardLayout
                background = resources.getDrawable(R.drawable.border) // Usar un drawable con borde
            }


            // Agregar la imagen (debe haber una imagen para cada palabra en drawable)
            val imageView = ImageView(this)
            val imageResId = resources.getIdentifier(word, "drawable", packageName)
            if (imageResId != 0) {
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.sobre_nosotros) // Imagen por defecto si no existe la imagen
            }
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200 // Ajustar el tamaño de la imagen
            ).apply {
                setMargins(0, 0, 0, 8) // Márgenes para separar la imagen del texto
            }
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE // Para centrar la imagen dentro del recuadro

            // Agregar el texto con el nombre de la palabra
            val button = Button(this).apply {
                text = word
                setBackgroundColor(Color.LTGRAY)
                setTextColor(Color.BLACK)
                textSize = 18f
                setPadding(16, 16, 16, 16)
            }

            // Configurar clic del botón
            button.setOnClickListener {
                if (selectedWords.size < 5) { // Limitar a 5 selecciones
                    if (selectedWords.contains(word)) {
                        selectedWords.remove(word)
                        button.setBackgroundColor(Color.LTGRAY)
                    } else {
                        selectedWords.add(word)
                        // Obtener los colores desde los recursos
                        val correctColor = resources.getColor(R.color.correct, null)
                        val incorrectColor = resources.getColor(R.color.incorrect, null)

                        if (list.contains(word)) {
                            button.setBackgroundColor(correctColor)  // Correcto, color verde
                        } else {
                            button.setBackgroundColor(incorrectColor)  // Incorrecto, color rojo
                        }
                    }
                } else {
                    // No permitir seleccionar más de 5 palabras
                    Toast.makeText(this, "Solo puedes seleccionar 5 palabras", Toast.LENGTH_SHORT).show()
                    if (selectedWords.contains(word)) {
                        selectedWords.remove(word)
                        button.setBackgroundColor(Color.LTGRAY)
                    }
                }
            }

            // Añadir la imagen y el botón al contenedor cardLayout
            cardLayout.addView(imageView)
            cardLayout.addView(button)

            // Añadir el cardLayout al GridLayout
            wordGrid.addView(cardLayout)
        }

        // Iniciar el temporizador para la selección
        startSelectionTimer()
    }

    // Temporizador para la selección de palabras
    private fun startSelectionTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(10000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = getString(R.string.time_remaining, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                checkAnswers()
            }
        }.start()
    }

    // Verifica las respuestas del jugador
    private fun checkAnswers() {
        val correctAnswers = currentList
        val correctSelections = selectedWords.filter { it in correctAnswers }

        // Guardar la puntuación de la ronda
        roundScores.add(correctSelections.size)

        // Aumentar la ronda
        currentRound++

        // Si se completaron las 3 rondas, muestra la puntuación final
        if (currentRound > 3) {
            showFinalScores()
        } else {
            startGame()
        }
    }

    // Redirige a la actividad de puntuación final
    private fun showFinalScores() {
        val totalScore = roundScores.sum()
        val round1 = roundScores.getOrElse(0) { 0 }
        val round2 = roundScores.getOrElse(1) { 0 }
        val round3 = roundScores.getOrElse(2) { 0 }

        // Redirigir a PantallaFinalActivity y pasar las puntuaciones
        val intent = Intent(this, PantallaFinalActivity::class.java)
        intent.putExtra("ROUND_1_SCORE", round1)
        intent.putExtra("ROUND_2_SCORE", round2)
        intent.putExtra("ROUND_3_SCORE", round3)
        intent.putExtra("TOTAL_SCORE", totalScore)

        // Iniciar la actividad de la pantalla final
        startActivity(intent)
        finish() // Cierra la actividad actual
    }

    // Finaliza el juego mostrando un mensaje
    private fun endGame() {
        wordText.text = getString(R.string.game_completed)
        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

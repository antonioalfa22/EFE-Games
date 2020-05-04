package com.efe.games.ui.sudoku

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.efe.games.R
import com.efe.games.controller.UserController
import com.efe.games.controller.sudoku.SudokuController

class SudokuActivity : AppCompatActivity() {

    private var dificultad: Int = 0
    private var tiempo: Boolean = true
    private var musica: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dificultad = intent.getIntExtra("dificultad", 1)
        tiempo = intent.getBooleanExtra("tiempo", true)
        musica = intent.getBooleanExtra("musica", true)
        this.setTheme(R.style.Theme_Default)
        setContentView(R.layout.activity_sudoku)

        val tiempoParaResolver = if(tiempo) when(dificultad) {
            0 -> 5*60000L
            1 -> 8*60000L
            2 -> 15*60000L
            3 -> 22*60000L
            else -> 30*60000L
        } else 0L
        // Crear partida
        SudokuController.iniciarPartida(this, findViewById(R.id.tablero), dificultad, tiempoParaResolver, tiempo)
        // Añadir teclado
        SudokuController.addTeclado(findViewById(R.id.teclado))
        SudokuController.activarTeclado()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.sudoku_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.undo -> {
                SudokuController.undo()
                return true
            }
            R.id.resolverSudoku -> {
                val builder: AlertDialog.Builder = this.let {
                    AlertDialog.Builder(it)
                }
                builder.setMessage("Si resuelves el sudoku no obtendrás puntos")
                    .setTitle("¿Quieres resolverlo?")
                builder.apply {
                    setPositiveButton("Ok") { _, _ ->
                        SudokuController.resolverSudoku()
                    }
                    setNegativeButton("Cancelar", null)
                }
                builder.create()
                builder.show()
                true
            }
            R.id.hayMusicaSudoku -> {
                true
            }
            R.id.ayudaSudoku -> {
                val builder: AlertDialog.Builder = this.let {
                    AlertDialog.Builder(it)
                }
                builder
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("Para jugar, simplemente debes rellenar las celdas en blanco de " +
                            "tal forma que cada fila, columna y " +
                            "caja de 3x3 no tenga números repetidos.")
                    .setTitle("Como jugar")
                builder.apply {
                    setPositiveButton("Ok", null)
                }
                builder.create()
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        SudokuController.onPause()
    }

    override fun onResume() {
        super.onResume()
        SudokuController.onResume()
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        builder
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Partida en curso")
            .setTitle("¿Estás seguro de que quieres salir")
        builder.apply {
            setPositiveButton("Si") { _, _ ->
                finish()
            }
            setNegativeButton("No", null)
        }
        builder.create()
        builder.show()
    }

    fun onSudokuResuelto(puntos: Long) {
        UserController.addPuntos(puntos)
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        builder
            .setIcon(android.R.drawable.checkbox_on_background)
            .setMessage("Has resuelto el sudoku correctamente.\n " +
                    "Como recompensa has ganado $puntos puntos")
            .setTitle("¡FELICIDADES!")
            .setCancelable(false)
        builder.apply {
            setPositiveButton("Salir") { _, _ ->
                finish()
            }
            setNegativeButton("Ver sudoku", null)
        }
        builder.create()
        builder.show()
    }

    fun onTiempoAgotado(){
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        builder
            .setIcon(android.R.drawable.checkbox_on_background)
            .setMessage("Se te ha agotado el tiempo para resolver el sudoku.\n " +
                    "Puedes seguir jugando pero no ganarás puntos")
            .setTitle("¡OH VAYA!")
            .setCancelable(false)
        builder.apply {
            setPositiveButton("Salir") { _, _ ->
                finish()
            }
            setNegativeButton("Seguir jugando", null)
        }
        builder.create()
        builder.show()
    }

}

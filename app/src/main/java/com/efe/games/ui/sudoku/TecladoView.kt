package com.efe.games.ui.sudoku

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import com.efe.games.R
import com.efe.games.model.sudoku.Celda
import com.efe.games.model.sudoku.SudokuGame
import com.efe.games.business.sudoku.escritura.MetodoEscritura
import com.efe.games.business.sudoku.escritura.PadNumerico
import com.efe.games.model.sudoku.listeners.OnCeldaSeleccionadaListener
import com.efe.games.model.sudoku.listeners.OnTocarCeldaListener


class TecladoView : LinearLayout {
    private var tablero: TableroSudokuView? = null
    private var game: SudokuGame? = null
    var metodosEscritura: MutableList<MetodoEscritura> = mutableListOf()
    var metodoActivo = -1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun initialize(tablero: TableroSudokuView?, game: SudokuGame?) {
        this.tablero = tablero
        this.tablero!!.onTocarCeldaListener = mOnCellTapListener
        this.tablero!!.onCeldaSeleccionadaListener = mOnCellSelected
        this.game = game
        crearMetodosEscritura()
    }


    private fun crearMetodosEscritura() {
        addMetodoEscritura(PadNumerico())
//        addMetodoEscritura(VentanaTeclado())
    }

    private fun addMetodoEscritura(im: MetodoEscritura) {
        im.inicializar(context, this, game, tablero)
        metodosEscritura.add(im)
    }

    fun activarMetodoEscritura(idMetodo: Int) {
        if (metodoActivo != -1) metodosEscritura[metodoActivo].deactivate()
        var encontrado = false
        var id = idMetodo
        var temp = 0
        if (id != -1) {
            while (!encontrado && temp <= metodosEscritura.size) {
                if (metodosEscritura[id].isEnabled) {
                    asegurarControlPanel(id)
                    encontrado = true
                    break
                }
                id++
                if (id == metodosEscritura.size) id = 0
                temp++
            }
        }
        if (!encontrado) id = -1
        for (i in metodosEscritura.indices) {
            val im: MetodoEscritura = metodosEscritura[i]
            if (im.isMetodoEscrituraViewCreated) im.metodoEscrituraView
                .setVisibility(if (i == id) View.VISIBLE else View.GONE)
        }
        metodoActivo = id
        if (metodoActivo != -1) {
            val activeMethod: MetodoEscritura = metodosEscritura[metodoActivo]
            activeMethod.activate()
        }
    }

    fun activateNextMetodoEscritura() {
        var id = metodoActivo + 1
        if (id >= metodosEscritura.size) id = 0
        activarMetodoEscritura(id)
    }

    fun pause() {
        for (im in metodosEscritura) {
            im.pause()
        }
    }

    /**
     * Garantiza la creación del panel de control para el método de entrada dado.
     *
     * @param id
     */
    private fun asegurarControlPanel(id: Int) {
        val im: MetodoEscritura = metodosEscritura[id]
        if (!im.isMetodoEscrituraViewCreated) {
            val tecladoView: View = im.metodoEscrituraView!!
            val cambiarModo: Button = tecladoView.findViewById(R.id.cambiarModo)
            cambiarModo.setOnClickListener(cambiarModoListener)
            this.addView(tecladoView)
        }
    }

    private val mOnCellTapListener: OnTocarCeldaListener = object : OnTocarCeldaListener {
        override fun onCellTapped(celda: Celda) {
            fun onCellTapped(celda: Celda?) {
                if (metodoActivo !== -1 && metodosEscritura != null) {
                    metodosEscritura[metodoActivo].onCellTapped(celda)
                }
            }
        }
    }

    private val mOnCellSelected: OnCeldaSeleccionadaListener =
        object : OnCeldaSeleccionadaListener {
            override fun onCellSelect(celda: Celda) {
                fun onCellSelected(celda: Celda?) {
                    if (metodoActivo !== -1 && metodosEscritura != null) {
                        metodosEscritura[metodoActivo].onCellSelected(celda)
                    }
                }
            }
        }

    private val cambiarModoListener =
        OnClickListener { activateNextMetodoEscritura() }

    companion object {
        const val INPUT_METHOD_POPUP = 0
        const val INPUT_METHOD_NUMPAD = 1
    }
}
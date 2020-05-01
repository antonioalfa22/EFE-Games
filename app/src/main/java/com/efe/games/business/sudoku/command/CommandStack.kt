package com.efe.games.business.sudoku.command

import com.efe.games.business.sudoku.SudokuManager
import java.util.*


class CommandStack {
    private val commandStack = Stack<EFECommand>()

    fun execute(command: EFECommand) {
        push(command)
        command.execute()
    }

    fun undo() {
        if (!commandStack.empty()) {
            val c = pop()
            c.undo()
            validarCeldas()
        }
    }

    fun setCheckpoint() {
        if (!commandStack.empty()) {
            val c = commandStack.peek()
            c.isCheckpoint = true
        }
    }

    fun hasCheckpoint(): Boolean {
        for (c in commandStack) if (c.isCheckpoint) return true
        return false
    }

    fun undoToCheckpoint() {
        var c: EFECommand
        while (!commandStack.empty()) {
            c = commandStack.pop()
            c.undo()
            if (commandStack.empty() || commandStack.peek().isCheckpoint) {
                break
            }
        }
        validarCeldas()
    }

    fun hasSomethingToUndo(): Boolean = commandStack.size != 0

    private fun push(command: EFECommand) = commandStack.push(command)

    private fun pop(): EFECommand = commandStack.pop()

    private fun validarCeldas() = SudokuManager.validarTablero()

}

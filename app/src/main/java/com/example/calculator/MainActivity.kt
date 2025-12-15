package com.example.calculator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false // on ne peut pas ajouter le symbole d'operation
    private var canAddDecimal = true // on peut ajouter le symbole d'operation

    private lateinit var operation: TextView // view qui affich l'operation
    private lateinit var results: TextView // view responsable a l'affichage de resultats de l'operation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
// initialisation des views
        operation = findViewById(R.id.operation)
        results = findViewById(R.id.results)
    }

    // ===== Buttons actions =====

    fun numberAction(view: View) {
        if (view is Button) { // si on click sur une bouton
            if (view.text == ".") {
                if (canAddDecimal) { // si le nombre n'est pas decimale (ne contient pas ".")
                    operation.append(".") // on ajoute vergule "."
                    canAddDecimal = false // pour n'accepte pas "." dans le meme chiffre
                }
            } else {
                operation.append(view.text) // on affiche l'operation a la view de l'operaton
            }
            canAddOperation = true // dans ce moment on peut ajout le symbole de l'operation
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) { // si on peut ajouter le symbole d'operation
            operation.append(view.text) // on ajoute le symbole
            canAddOperation = false // pour refuse de ajouter une autre symbole
            canAddDecimal = true // on peut ajouter un nombre decimale
        }
    }

    fun allClearAction(view: View) { // pour supprimer tous
        operation.text = ""
        results.text = ""
        canAddDecimal = true
        canAddOperation = false
    }

    fun backAction(view: View) { // pour suprimer le dernier caracter
        val text = operation.text.toString()
        if (text.isNotEmpty()) {
            val lastChar = text.last()
            operation.text = text.dropLast(1)
            if (lastChar == '.') canAddDecimal = true
        }
    }

    fun equalsAction(view: View) {
        results.text = calculateResults() // respensable a l'affichage de resultats finale
    }

    // ===== Calculation logic =====

    private fun calculateResults(): String {
        val list = digitsOperators()
        if (list.isEmpty()) return ""

        val timesDiv = timesDivisionCalculate(list)
        val result = addSubtractCalculate(timesDiv)
        return result.toString()
    }

    private fun addSubtractCalculate(list: MutableList<Any>): Float {
        var result = list[0] as Float
        for (i in list.indices) {
            if (list[i] is Char && i < list.lastIndex) {
                val op = list[i] as Char
                val next = list[i + 1] as Float
                if (op == '+') result += next
                if (op == '-') result -= next
            }
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun timesDivisionCalculate(list: MutableList<Any>): MutableList<Any> {
        var temp = list
        while (temp.contains('×') || temp.contains('/')) {
            temp = calcTimesDiv(temp)
        }
        return temp
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun calcTimesDiv(list: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var skip = false

        for (i in list.indices) {
            if (skip) {
                skip = false
                continue
            }

            if (list[i] is Char && i > 0 && i < list.lastIndex) {
                val op = list[i] as Char
                val prev = list[i - 1] as Float
                val next = list[i + 1] as Float

                when (op) {
                    '×' -> {
                        newList.removeLast()
                        newList.add(prev * next)
                        skip = true
                    }
                    '/' -> {
                        newList.removeLast()
                        newList.add(prev / next)
                        skip = true
                    }
                    else -> newList.add(op)
                }
            } else {
                newList.add(list[i])
            }
        }
        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var current = ""

        for (c in operation.text) {
            if (c.isDigit() || c == '.') {
                current += c
            } else {
                list.add(current.toFloat())
                current = ""
                list.add(c)
            }
        }
        if (current.isNotEmpty()) list.add(current.toFloat())
        return list
    }
}
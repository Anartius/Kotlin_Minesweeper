package minesweeper

import kotlin.random.Random

fun main() {
    val field = List(9) { MutableList(9) {'.'} }
    val random = Random

    val amountOfMines = getNumberOfMines()
    var i = 0
    while(i < amountOfMines) {
        val x = random.nextInt(0, 9)
        val y = random.nextInt(0, 9)
        if (field[x][y] != 'X') {
            field[x][y] = 'X'
            i++
        }
    }

    // Checking how many mines there is around every free spot
    for (x in field.indices) {
        for (y in field[x].indices) {
            if (field[x][y] == '.') {
                var amountOfMinesAround = 0
                for (i in 0..2) {
                    try {
                        if (field[x - 1][y - 1 + i] == 'X') amountOfMinesAround++
                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }

                if (y < field[x].lastIndex && field[x][y + 1] == 'X') {
                    amountOfMinesAround++
                }

                for (j in 0..2) {
                    try {
                        if (field[x + 1][y - 1 + j] == 'X') amountOfMinesAround++
                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }

                if (y > 0 && field[x][y - 1] == 'X') {
                    amountOfMinesAround++
                }

                if (amountOfMinesAround > 0) field[x][y] = amountOfMinesAround.toString().last()
            }
        }
    }

    field.forEach { println(it.joinToString("")) }
}

// Input amount of mines on the field and return it: Unit -> Int
fun getNumberOfMines() : Int {
    var amount: Int
    while (true) {
        println("How many mines do you want on the field?")
        amount = try {
            readLine()!!.toInt()
        } catch (e: NumberFormatException) {
            println("It's not a number, try again!")
            continue
        }
        if (amount < 1 || amount > 81) {
            println("You are out of range, number should be from 1 to 81!")
            continue
        }
        break
    }
    return amount
}
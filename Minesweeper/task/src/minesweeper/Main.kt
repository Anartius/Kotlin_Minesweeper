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

    field.forEach { println(it.joinToString("")) }
}

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
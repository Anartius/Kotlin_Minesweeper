package minesweeper

import kotlin.random.Random

class Cell {
    private var stateIcon = '.'
    private var minesAround = 0
    private var isFree = true
    private var isBomb = false
    private var isMarked = false
    private var isNumber = false

    fun setBomb() {
        stateIcon = '.'
        isBomb = true
        isFree = false
    }

    fun setNumber(n: Int) {
        if (n != -1) minesAround = n
        stateIcon = minesAround.toString().last()
        isNumber = true
    }

    fun mark() : Int {
        var score = 0
        stateIcon = if (!isMarked) {
            isMarked = !isMarked
            if (isBomb && isMarked) score++
            if (isFree && isMarked) score--
            '*'
        } else {
            if (isBomb) {
                isMarked = !isMarked
                score--
                'X'
            } else {
                isMarked = !isMarked
                score++
                '.'
            }
        }
        return score
    }

    fun getStateIcon() = stateIcon
    fun isFree() = isFree
    fun isBomb() = isBomb
    fun isNumber() = isNumber
}

fun main() {
    var field = List(9) { MutableList(9) { Cell() } }
    val random = Random
    var axes = mutableListOf(0, 0)
    var score = 0

    val amountOfMines = getNumberOfMines()
    var i = 0
    while(i < amountOfMines) {
        axes[0] = random.nextInt(0, 9)
        axes[1] = random.nextInt(0, 9)
        if (!field[axes[0]][axes[1]].isBomb()) {
            field[axes[0]][axes[1]].setBomb()
            i++
        }
    }

    field = checkMinesAround(field)
    printField(field)
    println()

    while (true) {
        while (true) {
            println("Set/delete mine marks (x and y coordinates):")
            try {
                axes = readLine()!!.split(" ").map { it.toInt() - 1 }.toMutableList()
                if (axes[0] < 0 || axes[0] > 8 || axes[1] < 0 || axes[1] > 8) {
                    println("You are out of range. Coordinates must be between 0 and 8")
                    continue
                }
                if (field[axes[1]][axes[0]].isNumber()) {
                    println("There is a number here!")
                    continue
                } else {
                    score += field[axes[1]][axes[0]].mark()
                    break
                }

            } catch (e: Exception) {
                println("Wrong input, try again!")
                continue
            }
        }

        printField(field)
        if (score == amountOfMines) {
            println("Congratulations! You found all the mines!")
            break
        }
        println()
    }
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

// Checking how many mines there is around every free spot:
// List<MutableList<Cell>> -> List<MutableList<Cell>>
fun checkMinesAround(field: List<MutableList<Cell>>) : List<MutableList<Cell>> {

    for (x in field.indices) {
        for (y in field[x].indices) {
            if (field[x][y].isFree()) {
                var amountOfMinesAround = 0
                for (i in 0..2) {
                    try {
                        if (field[x - 1][y - 1 + i].isBomb()) amountOfMinesAround++
                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }

                if (y < field[x].lastIndex && field[x][y + 1].isBomb()) {
                    amountOfMinesAround++
                }

                for (j in 0..2) {
                    try {
                        if (field[x + 1][y - 1 + j].isBomb()) amountOfMinesAround++
                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }

                if (y > 0 && field[x][y - 1].isBomb()) {
                    amountOfMinesAround++
                }

                if (amountOfMinesAround > 0) field[x][y].setNumber(amountOfMinesAround)
            }
        }
    }

    return field
}

//Printing field: List<MutableList<Cell>> -> Unit
fun printField(field: List<MutableList<Cell>>) {
    val tableHorizontalLine = "—│—————————│"

    println(" │123456789│")
    println(tableHorizontalLine)
    field.forEach {
        print("${field.indexOf(it) + 1}│")
        for (index in it.indices) {
            print(it[index].getStateIcon())
        }
        println("│")
    }
    println(tableHorizontalLine)
}
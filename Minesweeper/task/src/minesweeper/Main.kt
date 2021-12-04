package minesweeper

import java.lang.Exception
import kotlin.random.Random

class Cell {
    private var stateIcon = '.'
    private var minesAround = ""
    private var isFree = true
    private var isMine = false
    private var isMarked = false
    var isChecked = false

    fun setMine() {
        stateIcon = '.'
        isMine = true
        isFree = false
    }

    fun removeMine() {
        isMine = false
        isFree = true
    }

    fun setNumberOrFree(n: String) {
        minesAround = n
        stateIcon = minesAround.last()
    }

    fun mark(icon: Char) : Int {
        var score = 0
        stateIcon = if (!isMarked) {
            isMarked = !isMarked
            if (isMine && isMarked) score++
            if (isFree && isMarked) score--
            icon
        } else {
            if (isMine) {
                isMarked = !isMarked
                score--
                '.'
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
    fun isMine() = isMine
}

fun main() {
    var field = List(9) { MutableList(9) { Cell() } }
    var axes = mutableListOf(0, 0)
    var usersNextStep: MutableList<String>
    var score = 0
    var openOrMark: String
    var allClear = false
    var gameOver = false
    var firstFreeCell = true

    val amountOfMines = getNumberOfMines()
    field = putMines(field, amountOfMines)

    printField(field)

    while (true) {
        while (true) {
            print("""
                
                Set/unset mine marks or claim a cell as free: """.trimIndent())
            try {
                usersNextStep = readLine()!!.split(" ").toMutableList()
                println()
                axes = usersNextStep.subList(0, 2).map { it.toInt() - 1 }.reversed().toMutableList()
                openOrMark = usersNextStep[2]

                val currentCeLL = field[axes[0]][axes[1]]
                if (axes[0] < 0 || axes[0] > 8 || axes[1] < 0 || axes[1] > 8) {
                    println("You are out of range. Coordinates must be between 0 and 8")
                    continue
                }
                if (currentCeLL.isChecked) {
                    println("This cell is checked!")
                    continue
                } else {
                    if (openOrMark == "mine") {
                        score += currentCeLL.mark('*')

                        if (currentCeLL.getStateIcon() != '*') {
                            val surroundings = getSurroundings(field, axes, true)
                            for (k in surroundings.indices) {
                                if (field[surroundings[k][0]][surroundings[k][1]].getStateIcon() == '/') {
                                    checkMinesAround(field, axes)
                                    break
                                }
                            }
                        }
                        printField(field)
                        break

                    } else if(openOrMark == "free") {
                        if (firstFreeCell) {
                            field = firstFreeStep(field, axes)
                            firstFreeCell = false
                        }

                        if (currentCeLL.isMine()) {
                            gameOver= true
                            field = showAllMines(field)
                            printField(field)
                            println("""
                                
                                You stopped on a mine and failed!""".trimIndent())
                            break
                        }

                        field = checkMinesAround(field, axes)
                        printField(field)

                    } else {
                        println("Command to mark should be \"mine\" or \"free\"")
                    }
                }

            } catch (e: NumberFormatException) {
                println("Wrong number input, try again!")
                continue
            } catch (e: Exception) {
                println("Wrong input< try again!")
            }

            var closedCells = amountOfMines
            for (k in field.indices) {
                closedCells -= field[k].count { it.getStateIcon() == '.' ||
                        it.getStateIcon() == '*'
                }
            }
            if (closedCells == 0) {
                allClear = true
                break
            }
        }

        if (gameOver) break

        if (score == amountOfMines || allClear) {
            println("""
                
                Congratulations! You found all the mines!""".trimIndent())
            break
        }
    }
}

// Asking user about amount of mines on the field and return it as Int.
fun getNumberOfMines() : Int {
    var amount: Int
    while (true) {
        print("How many mines do you want on the field? ")
        amount = try {
            readLine()!!.toInt()
        } catch (e: NumberFormatException) {
            println("It's not a number, try again!")
            continue
        }
        println()
        if (amount < 1 || amount > 81) {
            println("You are out of range, number should be from 1 to 81!")
            continue
        }
        break
    }
    return amount
}

// Putting mines on the field. Takes field as List<MutableList<Cell>>,
// and amount of mines as Int, returns List<MutableList<Cell>>.
fun putMines(field: List<MutableList<Cell>>,
             amountOfMines: Int) : List<MutableList<Cell>> {

    val axes = mutableListOf(0, 0)
    val random = Random
    var i = 0

    while(i < amountOfMines) {
        axes[0] = random.nextInt(0, 9)
        axes[1] = random.nextInt(0, 9)
        if (!field[axes[0]][axes[1]].isMine()) {
            field[axes[0]][axes[1]].setMine()
            i++
        }
    }
    return field
}

// Checking how many mines there is around the cell,
// Takes field as List<MutableList<Cell>>, and coordinates as MutableList<Int>
// returns List<MutableList<Cell>>.
fun checkMinesAround(field: List<MutableList<Cell>>,
                    axes: MutableList<Int>) : List<MutableList<Cell>> {

    var thisField = field
    val currentCell = thisField[axes[0]][axes[1]]
    val surroundings = getSurroundings(field, axes, false)
    var cell: MutableList<Int>
    var amountOfMinesAround = 0
    var amountOfKnown = 0

    // Counting mines around current cell
    for (i in surroundings.indices) {
        cell = mutableListOf(surroundings[i][0], surroundings[i][1])

        if (thisField[cell[0]][cell[1]].getStateIcon() != '.' &&
            thisField[cell[0]][cell[1]].getStateIcon() != '*'
        ) {
            amountOfKnown++
        }

        if (thisField[cell[0]][cell[1]].isFree()) {
            continue
        } else {
            amountOfMinesAround++
        }
    }
    currentCell.isChecked = true

    if (amountOfMinesAround > 0) {
        currentCell.setNumberOrFree(amountOfMinesAround.toString())
        return thisField
    } else currentCell.setNumberOrFree("/")

    if (amountOfKnown == surroundings.size) {
        return thisField

    } else {
        // If current cell isn't a number and there is no undiscovered cells around,
        // discovering them.

        for (i in surroundings.indices) {
            cell = mutableListOf(surroundings[i][0], surroundings[i][1])
            if (thisField[cell[0]][cell[1]].isFree()) {
                thisField = checkMinesAround(thisField, surroundings[i])
            }
        }
    }
    return thisField

}

// Check for exist cells around certain cell.
// Takes field as List<MutableList<Cell>>, and coordinates as MutableList<Int>
// returns List<MutableList<Cell>>.
fun getSurroundings(field: List<MutableList<Cell>>,
                    axes: MutableList<Int>,
                    afterMine: Boolean) : MutableList<MutableList<Int>> {
    val possibleAxes = mutableListOf(
        mutableListOf(-1, -1), mutableListOf(-1, 0), mutableListOf(-1, 1),
        mutableListOf(0, 1), mutableListOf(1, 1), mutableListOf(1, 0),
        mutableListOf(1, -1), mutableListOf(0, -1),)

    val result = mutableListOf<MutableList<Int>>()
    for (i in possibleAxes.indices) {
        possibleAxes[i][0] = possibleAxes[i][0] + axes[0]
        possibleAxes[i][1] = possibleAxes[i][1] + axes[1]
        if (possibleAxes[i][0] in 0..8 &&
            possibleAxes[i][1] in 0..8 &&
            !field[possibleAxes[i][0]][possibleAxes[i][1]].isChecked
        ) {
            result.add(possibleAxes[i])
        } else if (
            afterMine &&
            possibleAxes[i][0] in 0..8 &&
            possibleAxes[i][1] in 0..8
        ) {
            result.add(possibleAxes[i])
        }
    }
    return result
}

// Printing field. Takes field as List<MutableList<Cell>>.
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
    print(tableHorizontalLine)
}

// Checking if first opened cell isn't a mine, if yes removes mine to another
// place. Takes field as List<MutableList<Cell>>, and coordinates as MutableList<Int>
// returns List<MutableList<Cell>>
fun firstFreeStep(field: List<MutableList<Cell>>,
                  axes: MutableList<Int>) : List<MutableList<Cell>> {

    val currentCeLL = field[axes[1]][axes[0]]
    var mineShifted = false

    if (currentCeLL.isMine()) {
        currentCeLL.removeMine()
        while(true) {
            for (y in field.indices) {
                for (x in field[y].indices) {
                    if (y == axes[1] && x == axes[0]) continue
                    if (!field[x][y].isMine() &&
                        field[x][y].getStateIcon() != '*'
                    ) {
                        field[x][y].setMine()
                        mineShifted = true
                        break
                    }
                    if (mineShifted) break
                }
                if (mineShifted) break
            }
            if (mineShifted) break
        }
    }
    return field
}

// Shows all mines on the field. Takes field as List<MutableList<Cell>>,
// returns List<MutableList<Cell>>
fun showAllMines (field: List<MutableList<Cell>>) : List<MutableList<Cell>> {
    for (i in field.indices) {
        for (j in field.indices) {
            if (field[i][j].isMine()) field[i][j].mark('X')
        }
    }
    return field
}
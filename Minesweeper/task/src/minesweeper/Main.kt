package minesweeper

import kotlin.random.Random

fun main() {
    val field = List(9) { MutableList(9) {'.'} }
    var minesPosition: Int
    for (i in field.indices) {
        minesPosition = Random.nextInt(0, 9)
        field[i][minesPosition] = 'X'
    }
    field.forEach { println(it.joinToString("")) }
}

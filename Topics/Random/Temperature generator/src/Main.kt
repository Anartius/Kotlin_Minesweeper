import kotlin.random.Random

fun generateTemperature(seed: Int): String {
    val random = Random(seed)
    var result = ""
    for (i in 0 until 7) {
        result += " ${random.nextInt(20, 31)}"
    }
    return result
}
import kotlin.random.Random

fun generatePredictablePassword(seed: Int): String {
    var randomPassword = ""
    // write your code here
    val random = Random(seed)
    for (i in 0 until 10) {
        randomPassword += random.nextInt(33, 127).toChar()
    }
	return randomPassword
}
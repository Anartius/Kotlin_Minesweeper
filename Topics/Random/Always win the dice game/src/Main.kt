import kotlin.random.Random

fun createDiceGameRandomizer(n: Int): Int {
    var seed = 1
    while (true) {
        val random = Random(seed)
        var myPoints = 0
        var myFriendsPoints = 0

        for (i in 0 until n) {
            myFriendsPoints += random.nextInt(1, 7)
            myPoints += random.nextInt(1, 7)
        }

        if (myPoints > myFriendsPoints) break
        seed++
    }
    return seed
}
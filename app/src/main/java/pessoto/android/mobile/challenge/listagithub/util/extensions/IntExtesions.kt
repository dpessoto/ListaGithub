package pessoto.android.mobile.challenge.listagithub.util.extensions

fun Int.toFormat(): String {
    try {
        var suffix = ""
        var n = this.toFloat()

        when {
            n >= 1000000000000L -> {
                n /= 1000000000000L
                suffix = "T"
            }
            n >= 1000000000 -> {
                n /= 1000000000
                suffix = "B"
            }
            n >= 1000000 -> {
                n /= 1000000
                suffix = "M"
            }
            n >= 1000 -> {
                n /= 1000
                suffix = "K"
            }
        }
        return String.format("%.2f", n) + suffix
    } catch (e: Exception) {
        return this.toString()
    }
}
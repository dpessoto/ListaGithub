package pessoto.android.mobile.challenge.listagithub.util.extensions

fun Long.toFormat(): String {
    try {
        var suffix = ""
        var n = this.toFloat()

        when {
            n >= 1000000000000L -> {
                n /= 1000000000000L
                suffix = "T"
            }
            n >= 1000000000L -> {
                n /= 1000000000L
                suffix = "B"
            }
            n >= 1000000L -> {
                n /= 1000000L
                suffix = "M"
            }
            n >= 1000L -> {
                n /= 1000L
                suffix = "K"
            }
            else -> String.format("%.f", n) + suffix
        }
        return String.format("%.1f", n) + suffix
    } catch (e: Exception) {
        return this.toString()
    }
}
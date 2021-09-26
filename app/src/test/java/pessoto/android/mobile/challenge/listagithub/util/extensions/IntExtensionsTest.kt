package pessoto.android.mobile.challenge.listagithub.util.extensions


import org.junit.Assert.assertEquals
import org.junit.Test

class IntExtensionsTest {

    @Test
    fun toFormatTeste() {
        assertEquals(100L.toFormat(), "100")
        assertEquals(1000L.toFormat(), "1,0K")
        assertEquals(1000000L.toFormat(), "1,0M")
        assertEquals(1000000000L.toFormat(), "1,0B")
        assertEquals(1000000000000L.toFormat(), "1,0T")
    }
}
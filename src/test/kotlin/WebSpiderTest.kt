
import org.junit.Test
import webSpider.getContentFromUrl
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestSource {
    @Test fun testGetContentFromUrl() {
        assertNotNull(getContentFromUrl("https://kotlinlang.org/"))
    }
}
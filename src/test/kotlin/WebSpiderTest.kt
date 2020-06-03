
import org.junit.Test
import webSpider.WebSpider
import webSpider.WebSpiderSettings
import kotlin.test.assertEquals

class WebSpiderTest {
    @Test fun integrationTest() {
        // given
        val webSpider = WebSpider(WebSpiderSettings(userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246"))
        val url = "https://kotlinlang.org/"

        // when
        webSpider.run(url)
        webSpider.stop()

        // then
        assertEquals(webSpider.getIndex().first(), url)
    }
}
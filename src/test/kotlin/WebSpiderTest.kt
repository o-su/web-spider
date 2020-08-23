
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import webSpider.WebSpider
import webSpider.WebSpiderSettings
import kotlin.test.assertEquals

class WebSpiderTest {
    @Test fun integrationTest() = runBlocking {
        // given
        val webSpider = WebSpider(
            WebSpiderSettings(
                userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
                threadCount = 1,
                maxDepth = 3,
                debug = true,
                domainRestriction = null,
                maxFileSize = null,
                minFileSize = null,
                targetDirectory = null,
                allowedFileExtensions = null
            )
        )
        val url = "https://kotlinlang.org/"

        // when
        launch(Dispatchers.IO) {
            webSpider.run(url)
        }
        launch(Dispatchers.IO) {
            webSpider.stop()
        }

        // then
        assertEquals(webSpider.getIndex().first(), url)
    }
}

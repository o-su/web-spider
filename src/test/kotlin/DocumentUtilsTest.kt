
import org.junit.Test
import webSpider.parseDocument
import webSpider.parseLinksFromDocument
import kotlin.test.assertEquals

class DocumentUtilsTest {
    @Test fun parseDocumentTest() {
        // given
        val content =
            """
        <html>
            <head>
                <title>TestTitle</title>
            </head>
        </html>
        """

        // when
        val document = parseDocument(content)

        // then
        assertEquals(document.title(), "TestTitle")
    }

    @Test fun parseLinksFromDocumentTest() {
        // given
        val content =
            """
        <html>
            <body>
                <a href="#">TestLink</a>
            </body>
        </html>
        """
        val document = parseDocument(content)

        // when
        val links = parseLinksFromDocument(document)

        // then
        assertEquals(links.first().text(), "TestLink")
    }
}


import org.junit.Test
import webSpider.*
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
        val document = parseDocument(content, "")

        // then
        assertEquals(document.title(), "TestTitle")
    }

    @Test fun parseElementsWithHrefAttributesFromDocumentTest() {
        // given
        val content =
            """
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="testStyle.css">
            </head>
            <body>
                <a href="#">TestLink</a>
            </body>
        </html>
        """
        val document = parseDocument(content, "")

        // when
        val elements = parseElementsWithHrefAttributesFromDocument(document)

        // then
        assertEquals("testStyle.css", elements.first().attr("href"))
        assertEquals("#", elements[1].attr("href"))
    }

    @Test fun parseElementsWithSrcAttributesFromDocumentTest() {
        // given
        val content =
            """
        <html>
            <head>
                <script src="testScript.js"></script>
            </head>
            <body>
                <script src="testScriptInsideBody.js"></script>
            </body>
        </html>
        """
        val document = parseDocument(content, "")

        // when
        val elements = parseElementsWithSrcAttributesFromDocument(document)

        // then
        assertEquals("testScript.js", elements.first().attr("src"))
        assertEquals("testScriptInsideBody.js", elements[1].attr("src"))
    }

    @Test fun disableScriptsTest() {
        // given
        val content =
            """
        <html>
            <head>
                <script src="testScript.js"></script>
            </head>
            <body>
                <script src="testScriptInsideBody.js"></script>
            </body>
        </html>
        """
        val baseUri = ""
        val document = parseDocument(content, baseUri)

        // when
        val documentWithDisabledScripts = disableScripts(document, baseUri)

        // then
        val expected =
            """<html>
 <head> 
  <!--<script src="testScript.js"></script>--> 
 </head> 
 <body> 
  <!--<script src="testScriptInsideBody.js"></script>-->   
 </body>
</html>"""
        assertEquals(
            expected,
            documentWithDisabledScripts.toString()
        )
    }

    @Test fun disableEventHandlersTest() {
        // given
        val content =
            """
        <html>
            <body onload="" onclick="alert('test')" >
            </body>
        </html>
        """
        val baseUri = ""
        val document = parseDocument(content, baseUri)

        // when
        val documentWithDisabledEventHandlers = disableEventHandlers(document, baseUri)

        // then
        val expected =
            """<html>
 <head></head>
 <body data-disabled-onload="" data-disabled-onclick="alert('test')">   
 </body>
</html>"""
        assertEquals(
            expected,
            documentWithDisabledEventHandlers.toString()
        )
    }
}

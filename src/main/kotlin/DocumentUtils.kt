package webSpider

import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun parseDocument(content: String, baseUri: String): Document {
    return Jsoup.parse(content, baseUri)
}

fun parseElementsWithHrefAttributesFromDocument(document: Document): List<Element> {
    return document.select("a[href], link[href]")
}

fun parseElementsWithSrcAttributesFromDocument(document: Document): List<Element> {
    return document.select("script[src], image[src]")
}

fun extractHrefAttributesFromElements(elements: List<Element>): List<String> {
    return elements.map { link ->
        link.absUrl("href")
    }
}

fun extractSrcAttributesFromElements(elements: List<Element>): List<String> {
    return elements.map { link ->
        link.absUrl("src")
    }
}

fun disableScripts(document: Document, baseUri: String): Document {
    val newDocument = document.clone()

    newDocument.select("script").forEach { scriptElement ->
        val clonedElement = scriptElement.clone()
        val comment = Comment(clonedElement.toString(), baseUri)
        scriptElement.after(comment)
        scriptElement.remove()
    }

    return newDocument
}

fun disableEventHandlers(document: Document, baseUri: String): Document {
    val newDocument = document.clone()

    newDocument.select("*").forEach { element ->
        element.attributes().forEach { attribute ->
            val attrKey: String = attribute.key

            if (attrKey.startsWith("on")) {
                attribute.setKey("data-disabled-" + attribute.key)
            }
        }
    }

    return newDocument
}

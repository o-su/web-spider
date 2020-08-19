package webSpider

import org.jsoup.Jsoup
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

package webSpider

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun parseDocument(content: String): Document {
    return Jsoup.parse(content)
}

fun parseLinksFromDocument(document: Document): List<Element> {
    return document.select("a[href]")
}

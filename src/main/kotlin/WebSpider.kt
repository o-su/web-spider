package webSpider

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main () {
    println("test")
}

fun getContentFromUrl(url: String): Document {
    val connection = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246")

    return connection.get()
}
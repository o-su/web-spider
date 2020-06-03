package webSpider

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main () {
    val webSpider = WebSpider(WebSpiderSettings(userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246"))

    webSpider.run("https://kotlinlang.org/")
}

data class WebSpiderSettings(val userAgent: String)

class WebSpider(private val settings: WebSpiderSettings) {
    private val index: ArrayList<String> = ArrayList()
    private val queue: ArrayList<String> = ArrayList()
    private var running: Boolean = false

    fun run(url: String) {
        this.index.add(url)
        this.queue.add(url)
        this.crawl()
        this.running = true
    }

    fun stop() {
        this.running = false
    }

    fun getIndex(): ArrayList<String> {
        return this.index
    }

    private fun crawl() {
        val url = this.getFirstUrlFromQueue()
        println(url)

        if (url.isNotEmpty() && this.running) {
            val content: Document = this.getPageContent(url)
            val links = this.parseLinksFromPageContent(content)

            this.addLinksToIndex(links)
            this.addLinksToQueue(links)
            this.crawl()
         }
    }

    private fun getPageContent(url: String): Document {
        val connection = Jsoup.connect(url).userAgent(this.settings.userAgent)

        return connection.get()
    }

    private fun parseLinksFromPageContent(content: Document): List<String> {
        return content.select("a[href]").map { link -> link.absUrl("href") }
    }

    private fun addLinksToIndex(links: List<String>) {
        for (link in links) {
            this.index.add(link)
        }
    }

    private fun addLinksToQueue(links: List<String>) {
        for (link in links) {
            this.queue.add(link)
        }
    }

    private fun getFirstUrlFromQueue(): String {
        return this.queue.removeAt(0)
    }
}
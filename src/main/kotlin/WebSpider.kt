package webSpider

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.Executors
import kotlinx.coroutines.*

 fun main() = runBlocking {
    val webSpider = WebSpider(WebSpiderSettings(userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (dispatcherKHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246", threadCount = 4))

     webSpider.run("https://kotlinlang.org/")
 }

data class WebSpiderSettings(val userAgent: String, val threadCount: Int)

class WebSpider(private val settings: WebSpiderSettings) {
    private val index: ArrayList<String> = ArrayList()
    private val queue: ArrayList<String> = ArrayList()
    private var running: Boolean = true
    private val dispatcher: ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(settings.threadCount).asCoroutineDispatcher()

    suspend fun run(url: String) {
        index.add(url)
        queue.add(url)
        running = true
        processQueue()
    }

    fun stop() {
        running = false
        this.dispatcher.close()
    }

    fun getIndex(): ArrayList<String> {
        return this.index
    }

    private suspend fun processQueue() {
        while(queue.isNotEmpty() && running) {
            crawl()
        }
    }

    private suspend fun crawl() =  withContext(this.dispatcher) {
        val url = getFirstUrlFromQueue()

        if (!url.isNullOrEmpty()) {
            val content: Document = getPageContent(url)
            val links = parseLinksFromPageContent(content)

            addLinksToIndex(links)
            addLinksToQueue(links)
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

    private fun getFirstUrlFromQueue(): String? {
        if (this.queue.isNotEmpty()) {
            return this.queue.removeAt(0)
        }

        return null
    }
}
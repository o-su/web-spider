package webSpider

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.concurrent.Executors

fun main() = runBlocking {
    val webSpider = WebSpider(
        WebSpiderSettings(
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (dispatcherKHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
            threadCount = 4,
            maxDepth = 3,
            debug = true
        )
    )

     webSpider.run("https://kotlinlang.org/")
 }

data class WebSpiderSettings(val userAgent: String, val threadCount: Int, val maxDepth: Short?, val debug: Boolean)
data class Link(val url: String, val depth: Int)

class WebSpider(private val settings: WebSpiderSettings) {
    private val index: ArrayList<String> = ArrayList()
    private val queue: ArrayList<Link> = ArrayList()
    private var running: Boolean = true
    private val dispatcher: ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(settings.threadCount).asCoroutineDispatcher()
    private val errorLog: ArrayList<String> = ArrayList()

    suspend fun run(url: String) {
        index.add(url)
        queue.add(Link(url = url, depth = 0))
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

    fun getErrorLog(): ArrayList<String> {
        return this.errorLog
    }

    private suspend fun processQueue() {
        while(queue.isNotEmpty() && running) {
            crawl()
        }
        stop()
    }

    private suspend fun crawl() =  withContext(this.dispatcher) {
        val link = getFirsLinkFromQueue()

        if (link !== null && settings.maxDepth !== null && link.depth <= settings.maxDepth) {
            val document: Document? = getPageContent(link.url)

            if (settings.debug) {
                println(link.url + link.depth)
            }

            if (document !== null) {
                val urls = parseLinksFromPageContent(document)
                val depth = link.depth + 1

                addLinksToIndex(urls)
                addLinksToQueue(urls.map { url -> Link(url, depth) })
            }
         }
    }

    private fun getPageContent(url: String): Document? {
        val connection = Jsoup.connect(url).userAgent(this.settings.userAgent)
        var document: Document? = null

        try {
            document = connection.get()
        } catch (exception: IOException) {
            this.logError(exception.toString())
        }

        return document
    }

    private fun parseLinksFromPageContent(content: Document): List<String> {
        return content.select("a[href]").map { link -> link.absUrl("href") }
    }

    private fun addLinksToIndex(links: List<String>) {
        for (link in links) {
            this.index.add(link)
        }
    }

    private fun addLinksToQueue(links: List<Link>) {
        for (link in links) {
            this.queue.add(link)
        }
    }

    private fun getFirsLinkFromQueue(): Link? {
        if (this.queue.isNotEmpty()) {
            return this.queue.removeAt(0)
        }

        return null
    }

    private fun logError(message: String) {
        this.errorLog.add(message)
    }
}
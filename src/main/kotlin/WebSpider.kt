package webSpider

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.commons.io.FilenameUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import java.net.*
import java.util.concurrent.Executors

fun main() = runBlocking {
    val webSpider = WebSpider(
        WebSpiderSettings(
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (dispatcherKHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
            threadCount = 4,
            maxDepth = 3,
            debug = true,
            domainRestriction = "kotlinlang.org",
            minFileSize = null,
            maxFileSize = null,
            targetDirectory = "C:\\\\Users\\dell\\Desktop\\crawlerTest\\"
        )
    )

    webSpider.run("https://kotlinlang.org/")
}

data class WebSpiderSettings(
    val userAgent: String,
    val threadCount: Int,
    val maxDepth: Short?,
    val debug: Boolean,
    val domainRestriction: String?,
    val minFileSize: Int?,
    val maxFileSize: Int?,
    val targetDirectory: String?
)

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
        while (queue.isNotEmpty() && running) {
            crawl()
        }
        stop()
    }

    private suspend fun crawl() = withContext(this.dispatcher) {
        val link = getFirsLinkFromQueue()

        if (link !== null && settings.maxDepth !== null && link.depth <= settings.maxDepth) {
            try {
                val fileContent = downloadFile(link.url, settings.minFileSize, settings.maxFileSize)
                val document: Document? = parseDocument(fileContent)

                if (settings.debug) {
                    println(link.url + link.depth)
                }

                if (document !== null) {
                    val urls = parseLinksFromPageContent(document)
                    val depth = link.depth + 1

                    addLinksToIndex(urls)
                    addLinksToQueue(urls.map { url -> Link(url, depth) })
                }

                if (settings.targetDirectory !== null) {
                    val uri = URI(link.url)
                    val host: String? = uri.host

                    saveContentToFile(settings.targetDirectory + host, resolveFilename(link.url), document.toString())
                }
            } catch (exception: Exception) {
                logError(exception.toString())
            }
        }
    }

    private fun resolveFilename(url: String): String {
        val filename = FilenameUtils.getName(url)

        if (filename.isEmpty()) {
            return "unknown"
        }

        return filename
    }

    private fun parseLinksFromPageContent(content: Document): List<String> {
        return this.filterLinks(parseLinksFromDocument(content))
            .map { link ->
                link.absUrl("href")
            }
    }

    private fun filterLinks(links: List<Element>): List<Element> {
        return links.filter { link -> this.isLinkAllowedForIndexing(link) }
    }

    private fun isLinkAllowedForIndexing(link: Element): Boolean {
        return try {
            val url: String = link.absUrl("href")
            val uri = URI(url)
            val hostname: String? = uri.host
            val domainRestrictionActive: Boolean = this.settings.domainRestriction !== null
            val domainSatisfiesRestriction: Boolean = domainRestrictionActive && hostname !== null && hostname == this.settings.domainRestriction
            val urlAlreadyIndexed: Boolean = this.index.contains(url)

            !urlAlreadyIndexed && (!domainRestrictionActive || domainSatisfiesRestriction)
        } catch (exception: URISyntaxException) {
            this.logError(exception.toString())
            false
        }
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

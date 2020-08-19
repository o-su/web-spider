package webSpider

import java.net.URL

fun parseBaseUrlFromFullUrl(fullUrl: String): String {
    val url = URL(fullUrl)
    return url.protocol + "://" + url.host
}

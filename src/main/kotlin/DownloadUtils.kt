package webSpider

import java.lang.Exception
import java.net.*

class MinFileSizeExceededException(message: String) : Exception(message)
class MaxFileSizeExceededException(message: String) : Exception(message)

fun downloadFile(urlAdd: String, minFileSize: Int?, maxFileSize: Int?): String {
    val url = URL(urlAdd)
    var contentLength = 0
    var content = ""

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"

        inputStream.bufferedReader().use {
            it.forEachLine { line ->
                contentLength += line.length

                if (maxFileSize !== null && contentLength > maxFileSize) {
                    throw MaxFileSizeExceededException(contentLength.toString())
                }

                content += line
            }
        }
    }

    if (minFileSize !== null && contentLength < minFileSize) {
        throw MinFileSizeExceededException(contentLength.toString())
    }

    return content
}

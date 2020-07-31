package webSpider

import java.io.File

fun saveContentToFile(dirPath: String, fileName: String, fileContent: String) {
    val directory = File(dirPath)

    if (!directory.exists()) {
        createDirectory(dirPath)
    }

    File(dirPath, fileName).writeText(fileContent)
}

fun createDirectory(path: String): Boolean {
    val file = File(path)

    return file.mkdir()
}

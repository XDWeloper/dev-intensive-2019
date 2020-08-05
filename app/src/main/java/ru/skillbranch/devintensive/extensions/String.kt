package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val result = this.trim()
    return when {
        result.length <= len -> result
        else -> "${result.take(len).trimEnd()}..."
    }
}

fun String.stripHtml(): String = this
    .replace(Regex("""<.*?>"""), "") // Remove HTML tags
    .replace(Regex("""&(#\d+?|\w+?);"""), "") // Remove HTML escape sequences
    .replace(Regex(""" +"""), " ") // Remove extra spaces

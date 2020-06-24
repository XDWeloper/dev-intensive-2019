package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.trim()?.split(" ")

        var firstName = parts?.getOrNull(0)
        if (firstName == "") firstName = null

        var lastName = parts?.getOrNull(1)
        if (lastName == "") lastName = null

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " ") = payload.map {
        val isUpper = it.isUpperCase()
        val transLetter = when (it.toLowerCase()) {
            'а' -> "a"
            'б' -> "b"
            'в' -> "v"
            'г' -> "g"
            'д' -> "d"
            'е', 'ё', 'э' -> "e"
            'ж' -> "zh"
            'з' -> "z"
            'и', 'й', 'ы' -> "i"
            'к' -> "k"
            'л' -> "l"
            'м' -> "m"
            'н' -> "n"
            'о' -> "o"
            'п' -> "p"
            'р' -> "r"
            'с' -> "s"
            'т' -> "t"
            'у' -> "u"
            'ф' -> "f"
            'х' -> "h"
            'ц' -> "c"
            'ч' -> "ch"
            'ш' -> "sh"
            'щ' -> "sh'"
            'ъ', 'ь' -> ""
            'ю' -> "yu"
            'я' -> "ya"
            ' ' -> divider
            else -> "$it"
        }
        if (isUpper) transLetter.capitalize() else transLetter
    }.joinToString("")

    fun toInitials(firstName: String?, lastName: String?) = when {
        (firstName.isNullOrBlank() && lastName.isNullOrBlank()) -> null
        firstName.isNullOrBlank() -> "${lastName?.trim()?.get(0)}".toUpperCase()
        lastName.isNullOrBlank() -> "${firstName.trim()[0]}".toUpperCase()
        else -> "${firstName.trim()[0]}${lastName.trim()[0]}".toUpperCase()
    }

    fun validateURL(url: CharSequence?): Boolean {
        val wrongNames = listOf(
            "enterprise",
            "features",
            "topics",
            "collections",
            "trending",
            "events",
            "marketplace",
            "pricing",
            "nonprofit",
            "customer-stories",
            "security",
            "login",
            "join"
        ).joinToString("|")

        val pattern = Regex("""^(https://)?(www\.)?github\.com/(?!($wrongNames)/?$)[\-\w]+/?$""")
        return url.isNullOrBlank() || pattern.matches(url)
    }
}
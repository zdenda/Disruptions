package eu.zkkn.android.disruptions.utils

import java.util.Locale


fun String.isValidLineName(): Boolean {
    return "[a-zA-Z0-9-_.~%]{1,40}".toRegex().matches(this)
}

fun String.capitalize(): String {
    // shortcut if it's empty or it already starts with a capital letter
    if (this.isEmpty() || this[0].isTitleCase()) return this
    return this.replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

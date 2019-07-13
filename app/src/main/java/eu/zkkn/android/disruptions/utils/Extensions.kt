package eu.zkkn.android.disruptions.utils


fun String.isValidLineName(): Boolean {
    return "[a-zA-Z0-9-_.~%]{1,900}".toRegex().matches(this)
}

package eu.zkkn.android.disruptions.utils


fun String.isValidLineName(): Boolean {
    return "[a-zA-Z0-9-_.~%]{1,40}".toRegex().matches(this)
}

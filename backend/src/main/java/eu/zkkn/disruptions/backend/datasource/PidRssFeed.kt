package eu.zkkn.disruptions.backend.datasource

import java.time.LocalDateTime


data class PidRssFeed(
    val title: String,
    val updated: LocalDateTime,
    val items: List<Item>
) {

    data class Item(
        val guid: String,
        val title: String,
        val timeInfo: String,
        val lines: List<String>
    )

}

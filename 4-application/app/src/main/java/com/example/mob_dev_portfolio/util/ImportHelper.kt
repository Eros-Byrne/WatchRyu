package com.example.mob_dev_portfolio.util

import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus
import org.w3c.dom.Element
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Helper to parse MyAnimeList XML export files.
 * This satisfies the requirement for manual file imports.
 */
object ImportHelper {

    fun parseMalXml(inputStream: InputStream): List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(inputStream)
            val nodes = doc.getElementsByTagName("anime")

            for (i in 0 until nodes.length) {
                val element = nodes.item(i) as Element
                
                val id = element.getElementsByTagName("series_animedb_id").item(0)?.textContent?.toInt() ?: 0
                val title = element.getElementsByTagName("series_title").item(0)?.textContent ?: "Unknown"
                val watched = element.getElementsByTagName("my_watched_episodes").item(0)?.textContent?.toInt() ?: 0
                val score = element.getElementsByTagName("my_score").item(0)?.textContent?.toInt() ?: 0
                val statusStr = element.getElementsByTagName("my_status").item(0)?.textContent ?: "Plan to Watch"

                val status = when (statusStr.lowercase()) {
                    "watching" -> AnimeStatus.WATCHING
                    "completed" -> AnimeStatus.COMPLETED
                    "on-hold", "onhold" -> AnimeStatus.ON_HOLD
                    "dropped" -> AnimeStatus.DROPPED
                    else -> AnimeStatus.PLAN_TO_WATCH
                }

                animeList.add(Anime(
                    id = id,
                    title = title,
                    imageUrl = "", // XML usually doesn't have URLs, Jikan will fill this on first refresh
                    episodesWatched = watched,
                    score = score,
                    status = status
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return animeList
    }
}

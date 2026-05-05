package com.example.mob_dev_portfolio.util

import com.example.mob_dev_portfolio.model.Anime
import java.lang.StringBuilder

/**
 * Helper class to generate XML and CSV data for export.
 * Follows the standard MyAnimeList XML format for compatibility.
 */
object ExportHelper {

    fun convertToMALXml(animeList: List<Anime>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
        sb.append("<myanimelist>\n")
        sb.append("  <myinfo>\n")
        sb.append("    <user_export_type>1</user_export_type>\n")
        sb.append("  </myinfo>\n")

        for (anime in animeList) {
            sb.append("  <anime>\n")
            sb.append("    <series_animedb_id>${anime.id}</series_animedb_id>\n")
            sb.append("    <series_title><![CDATA[${anime.title}]]></series_title>\n")
            sb.append("    <my_watched_episodes>${anime.episodesWatched}</my_watched_episodes>\n")
            sb.append("    <my_score>${anime.score}</my_score>\n")
            sb.append("    <my_status>${anime.status.name}</my_status>\n")
            sb.append("    <my_comments><![CDATA[${anime.personalReview}]]></my_comments>\n")
            sb.append("  </anime>\n")
        }

        sb.append("</myanimelist>")
        return sb.toString()
    }

    fun convertToCsv(animeList: List<Anime>): String {
        val sb = StringBuilder()
        sb.append("ID,Title,Episodes Watched,Total Episodes,Score,Status,Review\n")
        for (anime in animeList) {
            // Simple CSV escaping for titles/reviews with commas
            val escapedTitle = anime.title.replace(",", " ")
            val escapedReview = anime.personalReview.replace(",", " ").replace("\n", " ")
            sb.append("${anime.id},\"$escapedTitle\",${anime.episodesWatched},${anime.episodes},${anime.score},${anime.status.name},\"$escapedReview\"\n")
        }
        return sb.toString()
    }
}

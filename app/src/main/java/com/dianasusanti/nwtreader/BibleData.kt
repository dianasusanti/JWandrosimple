package com.dianasusanti.nwtreader

import android.content.Context
import org.json.JSONObject

/**
 * One entry in the book index: a code (matches the asset file name, e.g. "kejadian"),
 * the display name, and how many chapters it has.
 */
data class BookInfo(
    val code: String,
    val name: String,
    val chapters: Int
)

/**
 * Loads the book index once, then loads each book's verse text lazily (and caches it)
 * the first time that book is opened. Nothing is held in memory until the user asks for it.
 *
 * Expected asset layout:
 *   assets/bible/index.json          -> [{ "code": "kejadian", "name": "Kejadian", "chapters": 50 }, ...]
 *   assets/bible/<code>.json         -> { "1": { "1": "verse text", "2": "verse text" }, "2": { ... } }
 *
 * A book with no matching <code>.json file simply shows "text not available yet" --
 * the app never crashes on missing data, since you'll likely fill books in gradually.
 */
class BibleRepository(private val context: Context) {

    private val chapterCache = mutableMapOf<String, JSONObject?>()

    fun loadIndex(): List<BookInfo> {
        val raw = context.assets.open("bible/index.json").bufferedReader().use { it.readText() }
        val arr = org.json.JSONArray(raw)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            BookInfo(
                code = o.getString("code"),
                name = o.getString("name"),
                chapters = o.getInt("chapters")
            )
        }
    }

    /** Returns null if this book has no data file yet. */
    private fun loadBookJson(code: String): JSONObject? {
        if (chapterCache.containsKey(code)) return chapterCache[code]
        val result = try {
            val raw = context.assets.open("bible/$code.json").bufferedReader().use { it.readText() }
            JSONObject(raw)
        } catch (e: Exception) {
            null
        }
        chapterCache[code] = result
        return result
    }

    /**
     * Returns an ordered list of (verseNumber, verseText) for one chapter,
     * or null if this book/chapter has no data yet.
     */
    fun getChapter(code: String, chapter: Int): List<Pair<Int, String>>? {
        val book = loadBookJson(code) ?: return null
        val chapterObj = book.optJSONObject(chapter.toString()) ?: return null
        val verseNumbers = chapterObj.keys().asSequence().mapNotNull { it.toIntOrNull() }.sorted().toList()
        if (verseNumbers.isEmpty()) return null
        return verseNumbers.map { v -> v to chapterObj.getString(v.toString()) }
    }
}

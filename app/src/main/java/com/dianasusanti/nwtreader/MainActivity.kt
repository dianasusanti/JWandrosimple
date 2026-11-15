package com.dianasusanti.nwtreader

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var repo: BibleRepository
    private lateinit var books: List<BookInfo>

    private lateinit var spinnerBook: Spinner
    private lateinit var spinnerChapter: Spinner
    private lateinit var textBody: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private var currentBookIndex = 0
    private var currentChapter = 1
    private var suppressChapterCallback = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repo = BibleRepository(this)
        books = repo.loadIndex()

        spinnerBook = findViewById(R.id.spinnerBook)
        spinnerChapter = findViewById(R.id.spinnerChapter)
        textBody = findViewById(R.id.textChapterBody)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)

        spinnerBook.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, books.map { it.name }
        )

        spinnerBook.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                currentBookIndex = position
                currentChapter = 1
                populateChapterSpinner()
                renderChapter()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        spinnerChapter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (suppressChapterCallback) return
                currentChapter = position + 1
                renderChapter()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        btnPrev.setOnClickListener { step(-1) }
        btnNext.setOnClickListener { step(1) }

        // Restore last position (in-memory default: Genesis/Kejadian 1, chapter 1)
        populateChapterSpinner()
        renderChapter()
    }

    private fun currentBook(): BookInfo = books[currentBookIndex]

    private fun populateChapterSpinner() {
        val book = currentBook()
        val labels = (1..book.chapters).map { "Ps. $it" }
        spinnerChapter.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, labels
        )
        suppressChapterCallback = true
        spinnerChapter.setSelection((currentChapter - 1).coerceIn(0, book.chapters - 1))
        suppressChapterCallback = false
    }

    private fun step(delta: Int) {
        val book = currentBook()
        var newChapter = currentChapter + delta
        var newBookIndex = currentBookIndex

        if (newChapter < 1) {
            // go to previous book's last chapter
            if (currentBookIndex > 0) {
                newBookIndex = currentBookIndex - 1
                newChapter = books[newBookIndex].chapters
            } else {
                newChapter = 1
            }
        } else if (newChapter > book.chapters) {
            // go to next book's first chapter
            if (currentBookIndex < books.size - 1) {
                newBookIndex = currentBookIndex + 1
                newChapter = 1
            } else {
                newChapter = book.chapters
            }
        }

        currentChapter = newChapter
        if (newBookIndex != currentBookIndex) {
            currentBookIndex = newBookIndex
            spinnerBook.setSelection(currentBookIndex) // triggers populateChapterSpinner + renderChapter via listener
        } else {
            populateChapterSpinner()
            renderChapter()
        }
    }

    private fun renderChapter() {
        val book = currentBook()
        val verses = repo.getChapter(book.code, currentChapter)
        if (verses == null) {
            textBody.text = getString(R.string.no_data_title) + "\n\n" + getString(R.string.no_data_body)
            return
        }
        val sb = StringBuilder()
        sb.append(book.name).append(" ").append(currentChapter).append("\n\n")
        for ((num, text) in verses) {
            sb.append(num).append(". ").append(text).append("\n\n")
        }
        textBody.text = sb.toString().trim()
        findViewById<android.widget.ScrollView>(R.id.scrollText).scrollTo(0, 0)
    }
}

package com.example.booksaver.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.booksaver.R
import com.example.booksaver.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: BookmarkDatabase
    private lateinit var dao: BookmarkDao

    private lateinit var titleInput: EditText
    private lateinit var urlInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var showBtn: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var filterBtn: Button
    private lateinit var listView: ListView

    private val items = ArrayList<BookmarkEntity>()
    private lateinit var listAdapter: ArrayAdapter<String>
    private val categoryList = ArrayList<String>()
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = BookmarkDatabase.getInstance(this)
        dao = database.dao()

        initUi()
        refreshAllBookmarks()
        refreshCategorySpinner()
        initActions()
    }

    private fun initUi() {
        titleInput = findViewById(R.id.etTitle)
        urlInput = findViewById(R.id.etUrl)
        categoryInput = findViewById(R.id.etCategory)
        saveBtn = findViewById(R.id.btnSave)
        categorySpinner = findViewById(R.id.spinnerCategories)
        filterBtn = findViewById(R.id.btnFilter)
        showBtn = findViewById(R.id.btnShowAll)
        listView = findViewById(R.id.listViewBookmarks)

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listView.adapter = listAdapter

        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
    }

    private fun initActions() {

        saveBtn.setOnClickListener {
            val t = titleInput.text.toString()
            val u = urlInput.text.toString()
            val c = categoryInput.text.toString()

            if (t.isBlank() || u.isBlank() || c.isBlank()) {
                Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addNewBookmark(t, u, c)
        }

        filterBtn.setOnClickListener {
            val cat = categorySpinner.selectedItem?.toString() ?: return@setOnClickListener
            if (cat == "All") {
                Toast.makeText(this, "Choose a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            filterByCategory(cat)
        }

        showBtn.setOnClickListener {
            refreshAllBookmarks()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            if (position < items.size) {
                val url = items[position].bookmarkUrl
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    private fun addNewBookmark(t: String, u: String, c: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.addBookmark(
                BookmarkEntity(
                    bookmarkTitle = t,
                    bookmarkUrl = u,
                    bookmarkCategory = c
                )
            )
            refreshAllBookmarks()
            refreshCategorySpinner()

            runOnUiThread {
                titleInput.text.clear()
                urlInput.text.clear()
                categoryInput.text.clear()
                Toast.makeText(this@MainActivity, "Saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshAllBookmarks() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.fetchAll()
            items.clear()
            items.addAll(data)
            runOnUiThread { updateList() }
        }
    }

    private fun filterByCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.fetchByCategory(category)
            items.clear()
            items.addAll(data)
            runOnUiThread { updateList() }
        }
    }

    private fun refreshCategorySpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            val cats = dao.fetchCategories()
            categoryList.clear()
            categoryList.add("All")
            categoryList.addAll(cats)
            runOnUiThread { spinnerAdapter.notifyDataSetChanged() }
        }
    }

    private fun updateList() {
        val formatted = items.map { "${it.bookmarkTitle} — ${it.bookmarkCategory}" }
        listAdapter.clear()
        listAdapter.addAll(formatted)
        listAdapter.notifyDataSetChanged()
    }
}

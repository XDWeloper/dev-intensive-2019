package ru.skillbranch.devintensive.ui.archive

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.resolveColor
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.viewmodels.ArchiveViewModel

class ArchiveActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ArchiveViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        chatAdapter = ChatAdapter {
            val snackbar = Snackbar.make(rv_archive_list, "Click on ${it.title}", Snackbar.LENGTH_LONG)
            with(snackbar.view) {
                setBackgroundColor(resolveColor(R.attr.colorSnackbarBackground))
                val textView = findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(resolveColor(R.attr.colorSnackbarTextColor))
            }
            snackbar.show()
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val touchCallback = ChatItemTouchHelperCallback(chatAdapter) {
            val item = it
            viewModel.restoreFromArchive(item.id)
            val snackbar =
                Snackbar.make(
                    rv_archive_list,
                    "Вы точно хотите восстановить ${item.title} из архива?",
                    Snackbar.LENGTH_LONG
                )
            snackbar.setAction(R.string.archive_undo_string) { viewModel.addToArchive(item.id) }
            with(snackbar.view) {
                setBackgroundColor(resolveColor(R.attr.colorSnackbarBackground))
                val textView = findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(resolveColor(R.attr.colorSnackbarTextColor))
            }
            snackbar.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }
}

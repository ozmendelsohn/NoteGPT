package com.cam.notegpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cam.notegpt.Adapter.NoteAdapter
import com.cam.notegpt.Database.NoteDatabase
import com.cam.notegpt.Models.Note
import com.cam.notegpt.Models.NoteViewModel
import com.cam.notegpt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NoteAdapter.NotesItemClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    lateinit var selectedNotes: Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle the Intent
            val note = result.data?.getSerializableExtra("note") as Note
            if (note != null)
                viewModel.update(note)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the UI
        initUI()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)

        viewModel.allNotes.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = NoteAdapter(this, this)
        binding.recyclerView.adapter = adapter

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
                if (results.resultCode == RESULT_OK) {
                    val data = results.data
                    // Handle the Intent
                    val note = results.data?.getSerializableExtra("note") as Note
                    viewModel.insertNote(note)
                }
            }
        binding.fbAddNote.setOnClickListener {
            val intent = Intent(this, AddNote::class.java)
            getContent.launch(intent)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // filter as you type
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // filter as you type
                if (newText != null){
                    adapter.filterList(newText)
                }
                return false
            }
        })
    }

    override fun onItemClick(note: Note) {
        val intent = Intent(this@MainActivity, AddNote::class.java)
        intent.putExtra("current_note", note)
        updateNote.launch(intent)
    }

    override fun onLongItemClick(note: Note, CardView: CardView) {
        selectedNotes = note
        popUpDisplay(CardView)
    }
    private fun popUpDisplay(CardView: CardView) {
        val popup = PopupMenu(this, CardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNotes)
            return true
        }
        return false
    }
}


package com.cam.notegpt

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.cam.notegpt.Adapter.NetworkManager
import com.cam.notegpt.Models.Note
import com.cam.notegpt.databinding.ActivityAddNoteBinding
import com.cam.notegpt.utilts.CURRENT_NOTE
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdate = false
    private val networkManager = NetworkManager()
    private lateinit var progressBar: ProgressBar

    private fun createNoteAndFinishActivity(title: String, noteContent: String, formatter: SimpleDateFormat, isUpdate: Boolean) {
        val note = if (isUpdate && ::oldNote.isInitialized) {
            Note(oldNote.id, title, noteContent, formatter.format(Date()))
        } else {
            Note(null, title, noteContent, formatter.format(Date()))
        }
        val intent = Intent()
        intent.putExtra("note", note)
        setResult(Activity.RESULT_OK, intent)
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar // initialize progressBar

        try {
            oldNote = intent.getSerializableExtra(CURRENT_NOTE) as Note
            binding.etTitle.setText(oldNote.title)
            binding.etNote.setText(oldNote.note)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            var noteContent = binding.etNote.text.toString()

            if (title.isNotEmpty() || noteContent.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                }

                networkManager.rephraseNote(noteContent, object : NetworkManager.Callback {
                    override fun onSuccess(content: String) {
                        noteContent = content
                        if (title.isEmpty()) {
                            networkManager.generateTitle(noteContent, object : NetworkManager.Callback {
                                override fun onSuccess(generatedTitle: String) {
                                    createNoteAndFinishActivity(generatedTitle, noteContent, formatter, isUpdate)
                                }

                                override fun onError(e: Exception) {
                                    e.printStackTrace()
                                    runOnUiThread {
                                    Toast.makeText(applicationContext, "Failed to generate title. Please try again.", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.GONE
                                    }
                                }
                            })
                        } else {
                            createNoteAndFinishActivity(title, noteContent, formatter, isUpdate)
                        }
                    }

                    override fun onError(e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                        Toast.makeText(applicationContext, "Failed to rephrase note. Please try again.", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Please enter a title and note", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imgBackArrow.setOnClickListener {
            finish()
        }
    }
}

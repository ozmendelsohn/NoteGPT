package com.cam.notegpt

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.cam.notegpt.Database.NetworkRepository
import com.cam.notegpt.Models.Note
import com.cam.notegpt.databinding.ActivityAddNoteBinding
import com.cam.notegpt.utilts.CURRENT_NOTE
import com.cam.notegpt.utilts.OPENAI_API_KEY
import java.text.SimpleDateFormat
import java.util.Date
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject
import org.json.JSONException


class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdate = false
    private lateinit var progressBar: ProgressBar


    fun createNoteAndFinishActivity(title: String, noteContent: String, formatter: SimpleDateFormat, isUpdate: Boolean) {
        val note = if (isUpdate && ::oldNote.isInitialized) {
            Note(oldNote.id, title, noteContent, formatter.format(Date()))
        } else {
            Note(null, title, noteContent, formatter.format(Date()))
        }
        val intent = Intent()
        intent.putExtra("note", note)
        setResult(Activity.RESULT_OK, intent)
        // Run on UI thread
        runOnUiThread {
            // Hide progress bar when finished
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
        // save the note
        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            var noteContent = binding.etNote.text.toString()

            if (title.isNotEmpty() || noteContent.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                // Show progress bar before starting the network request
                progressBar.visibility = View.VISIBLE

                // Send network request here
                NetworkRepository().sendPostRequest(title, noteContent, OPENAI_API_KEY, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        val errorMessage = "Error occurred while making the post request: ${e.localizedMessage}"
                        noteContent += "\n\n$errorMessage"
                        createNoteAndFinishActivity(title, noteContent, formatter, isUpdate)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            val errorMessage = "Post request unsuccessful, response code: ${response.code}"
                            noteContent += "\n\n$errorMessage"
                        } else {
                            val responseData = response.body?.string()
                            try {
                                val json = JSONObject(responseData)
                                val choices = json.getJSONArray("choices")
                                val firstChoice = choices.getJSONObject(0)
                                val message = firstChoice.getJSONObject("message")
                                val content = message.getString("content")
                                // content now contains the value from the "content" field
                                noteContent = content
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                noteContent += "\nError parsing the response"
                            }
                        }
                        createNoteAndFinishActivity(title, noteContent, formatter, isUpdate)
                    }
                })
            } else {
                Toast.makeText(this, "Please enter a title and note", Toast.LENGTH_SHORT).show()
            }
        }

        // cancel the note creation
        binding.imgBackArrow.setOnClickListener {
            finish()
        }

    }
}
package com.cam.notegpt.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cam.notegpt.Models.Note
import com.cam.notegpt.R
import kotlin.random.Random

class NoteAdapter(private val context: Context, val listener: NotesItemClickListener ) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var NotesList = ArrayList<Note>()
    private var FullList = ArrayList<Note>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
        LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = NotesList[position]
        holder.title.text = currentNote.title
        holder.title.isSelected = true

        holder.note.text = currentNote.note

        holder.date.text = currentNote.date
        holder.date.isSelected = true

        holder.notes_layout.setCardBackgroundColor(context.resources.getColor(randomColor(), null))

        holder.notes_layout.setOnClickListener {
            listener.onItemClick(NotesList[holder.adapterPosition])
        }
        holder.notes_layout.setOnLongClickListener {
            listener.onLongItemClick(NotesList[holder.adapterPosition], holder.notes_layout)
            true
        }
    }
    
    override fun getItemCount(): Int {
        return NotesList.size
    }

    fun updateList(newList: List<Note>){
        FullList.clear()
        FullList.addAll(newList)
        NotesList.clear()
        NotesList.addAll(FullList)
        notifyDataSetChanged()
    }

    fun filterList(search: String) { // TODO: update this to do context search
        NotesList.clear()
        for (item in FullList) {
            if (item.title?.lowercase()?.contains(search.lowercase()) == true || item.note?.lowercase()?.contains(search.lowercase()) == true) {
                NotesList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    fun randomColor(): Int {
        val list = ArrayList<Int>()
        list.add(R.color.NoteColor1)
        list.add(R.color.NoteColor2)
        list.add(R.color.NoteColor3)
        list.add(R.color.NoteColor4)
        list.add(R.color.NoteColor5)
        list.add(R.color.NoteColor6)
        list.add(R.color.NoteColor7)
        list.add(R.color.NoteColor8)
        list.add(R.color.NoteColor9)
        val seed = System.currentTimeMillis()
        val randomIndex = Random(seed).nextInt(list.size)
        return list[randomIndex]
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val notes_layout = itemView.findViewById<CardView>(R.id.card_layout)
        val title = itemView.findViewById<TextView>(R.id.note_title)
        val note = itemView.findViewById<TextView>(R.id.note_text)
        val date = itemView.findViewById<TextView>(R.id.note_date)
    }

    interface NotesItemClickListener{
        fun onItemClick(note: Note)
        fun onLongItemClick(note: Note, CardView: CardView)
    }
}
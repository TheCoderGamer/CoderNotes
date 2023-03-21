package pmm.ignacio.codernotes.recyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.Note;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private final TextView _noteTextView;

    public NoteViewHolder(@NonNull View itemView, NoteAdapter.NoteClickListener noteClickListener) {
        super(itemView);
        _noteTextView = itemView.findViewById(R.id.note);
        ImageButton buttonDelete = itemView.findViewById(R.id.note_button_delete);
        buttonDelete.setOnClickListener(view -> noteClickListener.onNoteDelete(getAdapterPosition()));
        ImageButton buttonEdit = itemView.findViewById(R.id.note_button_details);
        buttonEdit.setOnClickListener(view -> noteClickListener.onNoteEdit(getAdapterPosition()));
    }

    public void bind(Note note) {
        _noteTextView.setText(note.title);
    }
}
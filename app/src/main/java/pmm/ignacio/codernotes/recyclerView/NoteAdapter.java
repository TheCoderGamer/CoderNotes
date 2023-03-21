package pmm.ignacio.codernotes.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.Note;


public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final List<Note> _notes;
    private final NoteClickListener _noteClickListener;

    public interface NoteClickListener {
        void onNoteEdit(int position);

        void onNoteDelete(int position);
    }

    public NoteAdapter(List<Note> notes, NoteClickListener noteClickListener) {
        _notes = notes;
        _noteClickListener = noteClickListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View noteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note_item, parent, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(noteView, _noteClickListener);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(_notes.get(position));
    }

    @Override
    public int getItemCount() {
        return _notes.size();
    }
}
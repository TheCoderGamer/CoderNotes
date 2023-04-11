package pmm.ignacio.codernotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pmm.ignacio.codernotes.db.AppDatabase;
import pmm.ignacio.codernotes.db.Note;
import pmm.ignacio.codernotes.db.TagWithNotes;
import pmm.ignacio.codernotes.recyclerView.NoteAdapter;

public class SearchActivity extends AppCompatActivity {

    // Search by title, content

    private static final String TAG = SearchActivity.class.getName();

    private List<Note> _notes;
    private List<Note> _notesFiltered = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter.NoteClickListener noteClickListener;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(getString(R.string.search));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;


        appDatabase.noteDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    _notes = notes;


                    recyclerView = findViewById(R.id.recyclerView);
                    noteClickListener = new NoteAdapter.NoteClickListener() {
                        @Override
                        public void onNoteEdit(int position) {
                            Log.i(TAG, "Editing note: " + _notesFiltered.get(position));
                            Note note = _notesFiltered.get(position);
                            Intent intent = new Intent();
                            intent.setClass(SearchActivity.this, EditNoteActivity.class);
                            intent.putExtra(EditNoteActivity.NOTE_ID_KEY, note.noteId);
                            startActivity(intent);
                        }

                        @Override
                        public void onNoteDelete(int position) {
                            Log.i(TAG, "Deleting note: " + _notesFiltered.get(position));
                            Note note = _notesFiltered.get(position);
                            appDatabase.noteDao().deleteNote(note)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        _notesFiltered.remove(position);
                                        _notes.remove(note);
                                        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(position);
                                    });
                        }
                    };

                    recyclerView.setAdapter(new NoteAdapter(_notesFiltered, noteClickListener));
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                });


        Button button = findViewById(R.id.search_button);
        button.setOnClickListener(v -> refreshRecyclerView());
    }

    private void refreshRecyclerView() {
        // Filter notes
        EditText editText = findViewById(R.id.search_text);
        String searchText = editText.getText().toString();
        if (_notesFiltered != null) {
            _notesFiltered.clear();
        }
        for (Note note : _notes) {
            if (note.title.contains(searchText) || note.note.contains(searchText)) {
                Objects.requireNonNull(_notesFiltered).add(note);
            }
        }

        recyclerView.setAdapter(new NoteAdapter(_notesFiltered, noteClickListener));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

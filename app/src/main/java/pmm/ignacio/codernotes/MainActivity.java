package pmm.ignacio.codernotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.disposables.Disposable;
import pmm.ignacio.codernotes.db.AppDatabase;
import pmm.ignacio.codernotes.db.Note;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private List<Note> _notes;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Notes");

        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
        appDatabase.noteDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    _notes = notes;
                    RecyclerView recyclerView = findViewById(R.id.recyclerview);
                    recyclerView.setAdapter(new NoteAdapter(_notes, new NoteAdapter.NoteClickListener() {
                        @Override
                        public void onNoteEdit(int position) {
                            Log.i(TAG, "Editing note: " + _notes.get(position));
                            Note note = _notes.get(position);
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, EditNoteActivity.class);
                            intent.putExtra(EditNoteActivity.NOTE_ID_KEY, note.noteId);
                            startActivity(intent);
                        }

                        @Override
                        public void onNoteDelete(int position) {
                            Log.i(TAG, "Deleting note: " + _notes.get(position));
                            Note note = _notes.get(position);
                            appDatabase.noteDao().deleteNote(note)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        _notes.remove(position);
                                        recyclerView.getAdapter().notifyItemRemoved(position);
                                    });
                        }
                    }));
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                });

        Button button = findViewById(R.id.new_note_button);
        button.setOnClickListener(view -> {
            createNoteInt();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_note) {
            createNoteInt();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNoteInt() {
        Log.i(TAG, "Creating new note");
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.NOTE_ID_KEY, 0);
        startActivity(intent);
    }
}
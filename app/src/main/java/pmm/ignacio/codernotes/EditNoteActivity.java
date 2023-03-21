package pmm.ignacio.codernotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pmm.ignacio.codernotes.db.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pmm.ignacio.codernotes.db.Note;

public class EditNoteActivity extends AppCompatActivity {

    public static final String TAG = EditNoteActivity.class.getName();
    public static final String NOTE_ID_KEY = "NOTE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;

        Consumer<Note> noteConsumer = note -> {

            EditText editNoteText = findViewById(R.id.edit_note);
            editNoteText.setText(note.note);

            EditText editNoteTitle = findViewById(R.id.edit_title);
            editNoteTitle.setText(note.title);

            // Save button
            findViewById(R.id.save_button).setOnClickListener(view -> {
                note.note = editNoteText.getText().toString();
                note.title = editNoteTitle.getText().toString();

                Log.i(TAG, "Saving note: " + note);

                Action navigateToMainActivityAction = () -> {
                    Intent intent = new Intent();
                    intent.setClass(EditNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                };

                if (note.noteId > 0) {
                    appDatabase.noteDao().updateNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);
                } else {
                    appDatabase.noteDao().insertNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);
                }
            });
        };

        int studentId = getIntent().getIntExtra(NOTE_ID_KEY, 0);
        if (studentId > 0) {
            appDatabase.noteDao().find(studentId).subscribeOn(Schedulers.io()).subscribe(noteConsumer);
        } else {
            try {
                noteConsumer.accept(new Note());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_note) {
            findViewById(R.id.save_button).performClick();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
package pmm.ignacio.codernotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;
import pmm.ignacio.codernotes.db.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pmm.ignacio.codernotes.db.Note;
import pmm.ignacio.codernotes.db.NoteTagCrossRef;
import pmm.ignacio.codernotes.db.NoteWithTags;
import pmm.ignacio.codernotes.db.NotesWithTagsDao;
import pmm.ignacio.codernotes.db.Tag;
import pmm.ignacio.codernotes.db.TagWithNotes;

public class EditNoteActivity extends AppCompatActivity {

    public static final String TAG = EditNoteActivity.class.getName();
    public static final String NOTE_ID_KEY = "NOTE_ID";

    private AppDatabase appDatabase;

    private List<Tag> _selectedTags = new ArrayList<>();
    private List<Tag> _allTags = new ArrayList<>();

    ChipGroup chipGroup;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        int noteId = getIntent().getIntExtra(NOTE_ID_KEY, 0);

        setTitle(noteId > 0 ? getString(R.string.edit_note_activity_title) : getString(R.string.new_note_activity_title));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        appDatabase = ((RoomApplication) getApplication()).appDatabase;
        // Get all tags
        appDatabase.tagsDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(tags -> {
            _allTags = tags;
            // Get selected tags of the note
            if (noteId > 0) {
                appDatabase.notesDao().findWithTags(noteId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(noteWithTags -> {
                    _selectedTags = noteWithTags.tags;
                    createChipGroup();
                });
            } else {
                createChipGroup();
            }
        });


        Consumer<Note> noteConsumer = note -> {

            EditText editNoteText = findViewById(R.id.edit_note);
            editNoteText.setText(note.note);

            EditText editNoteTitle = findViewById(R.id.edit_title);
            editNoteTitle.setText(note.title);

            // Save button
            findViewById(R.id.save_button).setOnClickListener(view -> {
                note.note = editNoteText.getText().toString();
                note.title = editNoteTitle.getText().toString();

                NoteWithTags noteWithTags = new NoteWithTags();
                noteWithTags.note = note;
                noteWithTags.tags = _selectedTags;

                Action navigateToMainActivityAction = () -> {
                    Intent intent = new Intent();
                    intent.setClass(EditNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                };

                // Save to database
                Log.i(TAG, "Saving note: " + note);
                if (note.noteId > 0) {
                    // - Update note -
                    appDatabase.notesWithTagsDao().updateNoteWithTags(noteWithTags).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);

//                    appDatabase.noteDao().updateNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);
                } else {
                    // - Insert note -
                    appDatabase.notesWithTagsDao().insertNoteWithTags(noteWithTags).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);

                }
            });
        };


        if (noteId > 0) {
            appDatabase.notesDao().find(noteId).subscribeOn(Schedulers.io()).subscribe(noteConsumer);
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
        } else if (item.getItemId() == R.id.menu_add_tag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.add_tag_dialog_title);
            builder.setMessage(R.string.add_tag_dialog_message);
            final EditText input = new EditText(EditNoteActivity.this);
            builder.setView(input);
            builder.setPositiveButton(R.string.create, (dialogInterface, i) -> {
                CreateNewTag(input.getText().toString());
                createChipGroup();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            builder.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void CreateNewTag(String tagName) {
        Tag tag = new Tag();
        tag.tag = tagName;
        tag.tagId = 0;
        _selectedTags.add(tag);
        _allTags.add(tag);
    }

    private void createChipGroup() {
        // Create chips tags
        chipGroup = findViewById(R.id.chip_tag_group);
        chipGroup.removeAllViews();
        for (Tag tag : _allTags) {
            Chip chip = new Chip(this);
            chip.setText(tag.tag);
            chip.setCheckable(true);
            // Esto no es muy eficiente, pero es lo que hay
            for (Tag selectedTag : _selectedTags) {
                if (selectedTag.tagId == tag.tagId) {
                    chip.setChecked(true);
                }
            }
            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    _selectedTags.add(tag);
                } else {
                    _selectedTags.remove(tag);
                }
            });
            chipGroup.addView(chip);
        }
    }
}
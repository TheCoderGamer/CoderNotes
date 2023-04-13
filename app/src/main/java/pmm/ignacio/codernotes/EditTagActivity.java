package pmm.ignacio.codernotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pmm.ignacio.codernotes.db.AppDatabase;
import pmm.ignacio.codernotes.db.Tag;
import pmm.ignacio.codernotes.db.TagWithNotes;
import pmm.ignacio.codernotes.recyclerView.TagAdapter;

public class EditTagActivity extends AppCompatActivity {

    private static final String TAG = EditTagActivity.class.getName();
    private List<TagWithNotes> _tagsWithNotes;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        setTitle(getString(R.string.edit_tag_activity_title));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;


            appDatabase.tagsDao().getTagsWithNotes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tagWithNotes -> {
                        _tagsWithNotes = tagWithNotes;

                        // TODO: Esto tiene que estar en este hilo ?
                        RecyclerView recyclerView = findViewById(R.id.tag_recyclerview);
                        recyclerView.setAdapter(new TagAdapter(_tagsWithNotes, new TagAdapter.TagClickListener() {
                            // ---
                            @Override
                            public void onTagEdit(int position) {
                                Log.i(TAG, "Editing tag: " + _tagsWithNotes.get(position));
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditTagActivity.this);
                                builder.setTitle(getString(R.string.edit_tag));
                                builder.setMessage(getString(R.string.edit_tag_message));
                                final EditText input = new EditText(EditTagActivity.this);
                                input.setText(_tagsWithNotes.get(position).tag.tag);
                                builder.setView(input);
                                builder.setPositiveButton(getString(R.string.edit), (dialog, which) -> {
                                    Log.i(TAG, "Editing tag");
                                    TagWithNotes tagWithNote = _tagsWithNotes.get(position);
                                    tagWithNote.tag.tag = input.getText().toString();

                                    appDatabase.tagsDao().updateTag(tagWithNote.tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                                    _tagsWithNotes.set(position, tagWithNote);
                                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(position);
                                });
                                builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                                builder.show();
                            }

                            @Override
                            public void onTagDelete(int position) {
                                Log.i(TAG, "Deleting tag: " + _tagsWithNotes.get(position));
                                TagWithNotes tagWithNote = _tagsWithNotes.get(position);
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditTagActivity.this);
                                builder.setTitle(getString(R.string.delete_tag));
                                builder.setMessage(getString(R.string.delete_tag_message) + " " + tagWithNote.tag + "?");
                                builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                                    Log.i(TAG, "Deleting tag");
                                    appDatabase.tagsDao().deleteTag(tagWithNote.tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                                    _tagsWithNotes.remove(position);
                                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(position);
                                });
                                builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                                builder.show();
                            }
                        }));
                        recyclerView.setLayoutManager(new LinearLayoutManager(EditTagActivity.this));
                    });
        Consumer<Tag> tagConsumer = tag -> {
            FloatingActionButton button = findViewById(R.id.add_tag_button);
            button.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditTagActivity.this);
                builder.setTitle(getString(R.string.add_tag));
                builder.setMessage(getString(R.string.add_tag_message));
                final EditText input = new EditText(EditTagActivity.this);
                builder.setView(input);
                builder.setPositiveButton(getString(R.string.add), (dialog, which) -> {
                    Log.i(TAG, "Adding tag");
                    tag.tag = input.getText().toString();

                    appDatabase.tagsDao().insertTag(tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                    _tagsWithNotes.add(new TagWithNotes(tag, new ArrayList<>()) {
                    });

                    RecyclerView recyclerView = findViewById(R.id.tag_recyclerview);
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRangeInserted(_tagsWithNotes.size() - 1, 1);
                });
                builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                builder.show();

            });

        };

        try {
            tagConsumer.accept(new Tag());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tag_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit_tags) {
            findViewById(R.id.add_tag_button).performClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

package pmm.ignacio.codernotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pmm.ignacio.codernotes.db.AppDatabase;
import pmm.ignacio.codernotes.db.Tag;
import pmm.ignacio.codernotes.recyclerView.TagAdapter;

public class EditTagActivity extends AppCompatActivity {

    private static final String TAG = EditTagActivity.class.getName();
    private List<Tag> _tags;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        setTitle("Edit Tag");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
        appDatabase.tagDao().getAllTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tags -> {
                    _tags = tags;

                    RecyclerView recyclerView = findViewById(R.id.tag_recyclerview);
                    recyclerView.setAdapter(new TagAdapter(_tags, new TagAdapter.TagClickListener() {
                        @Override
                        public void onTagEdit(int position) {
                            Log.i(TAG, "Editing tag: " + _tags.get(position));
                        }

                        @Override
                        public void onTagDelete(int position) {
                            Log.i(TAG, "Deleting tag: " + _tags.get(position));
                        }
                    }));
                    recyclerView.setLayoutManager(new LinearLayoutManager(EditTagActivity.this));
                });

        FloatingActionButton button = findViewById(R.id.add_tag_button);
        button.setOnClickListener(v -> {
            Log.i(TAG, "Adding new tag");
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

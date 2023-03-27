package pmm.ignacio.codernotes.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.Tag;
import pmm.ignacio.codernotes.db.TagWithNotes;


public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private final List<TagWithNotes> _tagsWithNotes;
    private final TagClickListener _tagClickListener;

    public interface TagClickListener {
        void onTagEdit(int position);

        void onTagDelete(int position);
    }

    public TagAdapter(List<TagWithNotes> tagsWithNotes, TagClickListener tagClickListener) {
        this._tagsWithNotes = tagsWithNotes;
        _tagClickListener = tagClickListener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tagView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_item, parent, false);
        return new TagViewHolder(tagView, _tagClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(_tagsWithNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return _tagsWithNotes.size();
    }
}
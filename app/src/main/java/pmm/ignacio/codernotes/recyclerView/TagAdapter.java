package pmm.ignacio.codernotes.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.Tag;


public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private final List<Tag> _tags;
    private final TagClickListener _tagClickListener;

    public interface TagClickListener {
        void onTagEdit(int position);

        void onTagDelete(int position);
    }

    public TagAdapter(List<Tag> _tags, TagClickListener tagClickListener) {
        this._tags = _tags;
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
        holder.bind(_tags.get(position));
    }

    @Override
    public int getItemCount() {
        return _tags.size();
    }
}
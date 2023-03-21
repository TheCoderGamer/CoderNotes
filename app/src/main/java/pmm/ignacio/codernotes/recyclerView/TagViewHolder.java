package pmm.ignacio.codernotes.recyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.Tag;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private final TextView _tagTextView;

    public TagViewHolder(@NonNull View itemView, TagAdapter.TagClickListener tagClickListener) {
        super(itemView);
        _tagTextView = itemView.findViewById(R.id.tag);
        ImageButton buttonDelete = itemView.findViewById(R.id.tag_button_delete);
        buttonDelete.setOnClickListener(view -> tagClickListener.onTagDelete(getAdapterPosition()));
        ImageButton buttonEdit = itemView.findViewById(R.id.tag_button_details);
        buttonEdit.setOnClickListener(view -> tagClickListener.onTagEdit(getAdapterPosition()));
    }

    public void bind(Tag tag) {
        _tagTextView.setText(tag.tag);
    }
}
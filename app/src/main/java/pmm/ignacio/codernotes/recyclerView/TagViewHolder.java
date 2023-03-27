package pmm.ignacio.codernotes.recyclerView;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import pmm.ignacio.codernotes.R;
import pmm.ignacio.codernotes.db.AppDatabase;
import pmm.ignacio.codernotes.db.Tag;
import pmm.ignacio.codernotes.db.TagWithNotes;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private final TextView _tagTextView;
    private final TextView _tagNotesCount;

    public TagViewHolder(@NonNull View itemView, TagAdapter.TagClickListener tagClickListener) {
        super(itemView);
        _tagTextView = itemView.findViewById(R.id.tag);
        _tagNotesCount = itemView.findViewById(R.id.tag_count);
        ImageButton buttonDelete = itemView.findViewById(R.id.tag_button_delete);
        buttonDelete.setOnClickListener(view -> tagClickListener.onTagDelete(getAdapterPosition()));
        ImageButton buttonEdit = itemView.findViewById(R.id.tag_button_details);
        buttonEdit.setOnClickListener(view -> tagClickListener.onTagEdit(getAdapterPosition()));
    }

    public void bind(TagWithNotes tagWithNotes) {
        _tagTextView.setText(tagWithNotes.tag.tag);
        _tagNotesCount.setText(String.valueOf(tagWithNotes.notes.size()));
    }
}
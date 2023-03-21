package pmm.ignacio.codernotes.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class NoteWithTags {
    @Embedded
    public Note note;
    @Relation(
            parentColumn = "noteId",
            entityColumn = "tagId",
            associateBy = @Junction(NoteTagCrossRef.class)
    )
    public List<Tag> tags;
}

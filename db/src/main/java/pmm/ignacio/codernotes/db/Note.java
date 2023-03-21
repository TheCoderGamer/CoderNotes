package pmm.ignacio.codernotes.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int noteId;
    @ColumnInfo
    public String note;
    public String title;

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", note='" + note + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
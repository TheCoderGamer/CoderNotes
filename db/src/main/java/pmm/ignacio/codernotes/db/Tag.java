package pmm.ignacio.codernotes.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tags")
public class Tag {
    @PrimaryKey(autoGenerate = true)
    public int tagId;
    @ColumnInfo
    public String tag;
}

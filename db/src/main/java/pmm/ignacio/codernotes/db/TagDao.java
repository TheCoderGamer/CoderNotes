package pmm.ignacio.codernotes.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TagDao {
    @Query("SELECT * FROM tags")
    Single<List<Tag>> getAllTags();

    @Query("SELECT * FROM tags WHERE tagId = :tagId")
    Single<Tag> find(int tagId);

    @Transaction
    @Query("SELECT * FROM tags")
    Single<List<TagWithNotes>> getTagsWithNotes();

    @Transaction
    @Query("SELECT * FROM tags WHERE tagId = :tagId")
    Single<TagWithNotes> findWithNotes(int tagId);

    @Insert
    Completable insertTag(Tag tag);

    @Update
    Completable updateTag(Tag tag);

    @Delete
    Completable deleteTag(Tag tag);


}

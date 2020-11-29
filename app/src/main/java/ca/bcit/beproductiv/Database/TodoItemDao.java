package ca.bcit.beproductiv.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoItemDao {
    @Query("SELECT * FROM todoitem")
    LiveData<List<TodoItem>> getAll();

    @Query("SELECT * FROM todoitem")
    List<TodoItem> getAllOnce();

    @Query("SELECT * FROM todoitem WHERE uid IN (:todoIds)")
    LiveData<List<TodoItem>> loadAllByIds(int[] todoIds);

    @Query("SELECT * FROM todoitem WHERE todo_item_name LIKE :todoName LIMIT 1")
    TodoItem findByName(String todoName);

    @Query("SELECT * FROM todoitem WHERE uid == :my_uid LIMIT 1")
    TodoItem findByUID(int my_uid);

    @Query("SELECT * FROM todoitem WHERE uid == :my_uid LIMIT 1")
    LiveData<TodoItem> findByUIDLiveData(int my_uid);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TodoItem item);

    @Insert
    void insertAll(TodoItem... tasks);

    @Delete
    void delete(TodoItem task);
}
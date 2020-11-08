package ca.bcit.beproductiv.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TodoItemDao {
    @Query("SELECT * FROM todoitem")
    List<TodoItem> getAll();

    @Query("SELECT * FROM todoitem WHERE uid IN (:todoIds)")
    List<TodoItem> loadAllByIds(int[] todoIds);

    @Query("SELECT * FROM todoitem WHERE todo_item_name LIKE :todoName LIMIT 1")
    TodoItem findByName(String todoName);

    @Insert
    void insertAll(TodoItem... tasks);

    @Delete
    void delete(TodoItem task);
}
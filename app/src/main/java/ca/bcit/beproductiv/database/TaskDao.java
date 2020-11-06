package ca.bcit.beproductiv.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE uid IN (:taskIds)")
    List<Task> loadAllByIds(int[] taskIds);

    @Query("SELECT * FROM task WHERE task_name LIKE :taskName LIMIT 1")
    Task findByName(String taskName);

    @Insert
    void insertAll(Task... tasks);

    @Delete
    void delete(Task task);
}
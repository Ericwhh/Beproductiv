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
public interface TimerDataDao {
//    @Query("SELECT CASE WHEN EXISTS ( SELECT * FROM timerdata WHERE timer_item NOT NULL) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END")
//    boolean hasItemSelected();

    @Query("SELECT timer_todo_uid FROM timerdata LIMIT 1")
    LiveData<Integer> getTodoItemUID();

    @Query("SELECT timer_todo_uid FROM timerdata LIMIT 1")
    Integer getTodoItemUIDOnce();

    @Query("UPDATE timerdata SET timer_todo_uid = :todoItemUID")
    void setTodoItemUID(int todoItemUID);

    @Query("SELECT COUNT(timer_todo_uid) FROM timerdata")
    int getRowCount();

    @Insert
    void insert(TimerData timerData);
}
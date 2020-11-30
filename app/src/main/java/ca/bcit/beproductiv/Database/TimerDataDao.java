package ca.bcit.beproductiv.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TimerDataDao {
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
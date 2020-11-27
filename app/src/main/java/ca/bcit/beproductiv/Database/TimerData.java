package ca.bcit.beproductiv.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TimerData {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "timer_todo_uid")
    public int TodoItemUID;

    public TimerData() {}
    public TimerData(int todoItemUID) {
        this.TodoItemUID = todoItemUID;
    }
}

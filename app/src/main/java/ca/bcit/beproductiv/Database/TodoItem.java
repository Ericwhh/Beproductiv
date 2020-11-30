package ca.bcit.beproductiv.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collections;

@Entity
public class TodoItem {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "todo_item_name")
    public final String name;

    @ColumnInfo(name = "todo_item_description")
    public final String description;

    @ColumnInfo(name = "todo_item_is_complete")
    public boolean isComplete;

    public TodoItem(String name, String description) {
        this.name = name;
        this.description = description;
        this.isComplete = false;
    }

    public void setUID(int uid) {
        this.uid = uid;
    }

    public boolean getIsComplete() {
        return this.isComplete;
    }
}

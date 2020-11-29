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
    public String name;

    @ColumnInfo(name = "todo_item_description")
    public String description;

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

    public static TodoItem[] dummyItems = {
            new TodoItem("Fight a bear", "Fight a bear next Tuesday together with Albert"),
            new TodoItem("Swim in ocean", "Swim with the fishes"),
            new TodoItem("Get a six pack", "Push enough rocks to get a six pack")

    };

    public static ArrayList<TodoItem> getDummyData() {
        ArrayList<TodoItem> items = new ArrayList<>();
        Collections.addAll(items, dummyItems);
        return items;
    }

    public boolean getIsComplete() {
        return this.isComplete;
    }
}

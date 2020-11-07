package ca.bcit.beproductiv.Database;

import java.util.ArrayList;
import java.util.Collections;

public class TodoItem {
    public String name;
    public String description;
    public boolean isComplete;

    TodoItem(String name, String description) {
        this.name = name;
        this.description = description;
        this.isComplete = false;
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
}

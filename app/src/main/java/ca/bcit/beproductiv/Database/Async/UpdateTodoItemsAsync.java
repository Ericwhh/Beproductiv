package ca.bcit.beproductiv.Database.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class UpdateTodoItemsAsync extends AsyncTask<TodoItem, Void, Integer> {
    private final WeakReference<Context> contextRef;

    public UpdateTodoItemsAsync(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(TodoItem... todoItems) {
        AppDatabase db = AppDatabase.getInstance(contextRef.get());
        TodoItemDao dao = db.getTaskDao();

        for (TodoItem item : todoItems) {
            dao.update(item);
        }

        return 0;
    }
}
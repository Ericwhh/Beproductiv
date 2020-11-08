package ca.bcit.beproductiv.Database.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class GetTodoItemsAsync extends AsyncTask<String, Void, TodoItem> {
    private final WeakReference<Context> contextRef;

    public GetTodoItemsAsync(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected TodoItem doInBackground(String... todoItemNames) {
        AppDatabase db = AppDatabase.getInstance(contextRef.get());
        TodoItemDao dao = db.getTaskDao();

        return dao.findByName(todoItemNames[0]);
    }
}
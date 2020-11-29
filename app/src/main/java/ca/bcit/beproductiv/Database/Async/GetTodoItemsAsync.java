package ca.bcit.beproductiv.Database.Async;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.List;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class GetTodoItemsAsync extends AsyncTask<Void, Void, LiveData<List<TodoItem>>> {
    private final WeakReference<Context> contextRef;

    public GetTodoItemsAsync(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected LiveData<List<TodoItem>> doInBackground(Void ...voids) {
        AppDatabase db = AppDatabase.getInstance(contextRef.get());
        return db.getTaskDao().getAll();
    }
}
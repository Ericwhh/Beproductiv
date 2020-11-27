package ca.bcit.beproductiv.Database.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class RemoveTodoItemsAsync extends AsyncTask<Integer, Void, Integer> {
    private final WeakReference<Context> contextRef;

    public RemoveTodoItemsAsync(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(Integer... todoUIDs) {
        AppDatabase db = AppDatabase.getInstance(contextRef.get());
        TodoItemDao dao = db.getTaskDao();

        Integer selectedTodoUID = db.getTimerDataDao().getTodoItemUIDOnce();

        for (Integer itemUID : todoUIDs) {
            TodoItem item = dao.findByUID(itemUID);
            dao.delete(item);

            if (selectedTodoUID.equals(itemUID)) {
                db.getTimerDataDao().setTodoItemUID(-1);
            }
        }

        return 0;
    }
}
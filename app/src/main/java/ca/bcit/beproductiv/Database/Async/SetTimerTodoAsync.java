package ca.bcit.beproductiv.Database.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TimerDataDao;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class SetTimerTodoAsync extends AsyncTask<Integer, Void, Integer> {
    private final WeakReference<Context> contextRef;

    public SetTimerTodoAsync(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(Integer... todoUIDs) {
        AppDatabase db = AppDatabase.getInstance(contextRef.get());
        TimerDataDao dao = db.getTimerDataDao();

        for (Integer item : todoUIDs) {
            dao.setTodoItemUID(item);
        }

        return 0;
    }
}

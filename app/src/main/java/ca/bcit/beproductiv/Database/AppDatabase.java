package ca.bcit.beproductiv.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TodoItem.class, TimerData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "todo_database.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }

        TimerDataDao timerDataDao = instance.getTimerDataDao();
        if (timerDataDao.getRowCount() == 0) {
            timerDataDao.insert(new TimerData(-1));
        }

        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract TodoItemDao getTaskDao();
    public abstract TimerDataDao getTimerDataDao();
}
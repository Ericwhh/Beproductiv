package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.florent37.materialviewpager.MaterialViewPager;

import java.lang.ref.WeakReference;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;
import ca.bcit.beproductiv.Tabs.TimerFragment;
import ca.bcit.beproductiv.Tabs.TodoFragment;



public class HomeActivity extends AppCompatActivity {

    public AppDatabase _database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _database = AppDatabase.getInstance(getApplicationContext());

        TodoItem t1 = new TodoItem("Item 1", "Description 1");
        TodoItem t2 = new TodoItem("Item 2", "Description 2");
        TodoItem t3 = new TodoItem("Item 3", "Description 3");
        TodoItem t4 = new TodoItem("Item 4", "Description 4");

        new AddTodoItemsAsync(getApplicationContext()).execute(t1, t2, t3, t4);

        SectionsPageAdapter pagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

//        Button settingsButton = findViewById(R.id.settingsButton);
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, Settings.class);
//                startActivity(intent);
//            }
//        });


    }

    private static class AddTodoItemsAsync extends AsyncTask<TodoItem, Void, Integer> {
        private final WeakReference<Context> contextRef;

        public AddTodoItemsAsync(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(TodoItem... todoItems) {
            AppDatabase db = AppDatabase.getInstance(contextRef.get());
            TodoItemDao dao = db.getTaskDao();

            for (TodoItem item : dao.getAll()) {
                dao.delete(item);
            }

            db.getTaskDao().insertAll(todoItems);

            return 0;
        }
    }

    public static class SectionsPageAdapter extends FragmentPagerAdapter {
        public SectionsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TimerFragment();
                case 1:
                    return new TodoFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Timer";
                case 1:
                    return "To-do";
            }
            return null;
        }
    }
}
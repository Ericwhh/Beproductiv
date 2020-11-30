package ca.bcit.beproductiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Tabs.SettingsFragment;
import ca.bcit.beproductiv.Tabs.TimerFragment;
import ca.bcit.beproductiv.Tabs.TodoFragment;



public class HomeActivity extends AppCompatActivity {

    public AppDatabase _database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _database = AppDatabase.getInstance(getApplicationContext());

        SectionsPageAdapter pagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        Button settingsButton = findViewById(R.id.btnOpenFaq);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FaqActivity.class);
            startActivity(intent);
        });
    }

    public static class SectionsPageAdapter extends FragmentPagerAdapter {
        public SectionsPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TimerFragment();
                case 1:
                    return new TodoFragment();
                case 2:
                    return new SettingsFragment();
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
                case 2:
                    return "Settings";
            }
            return null;
        }
    }
}
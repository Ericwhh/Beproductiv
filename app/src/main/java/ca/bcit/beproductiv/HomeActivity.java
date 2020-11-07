package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.florent37.materialviewpager.MaterialViewPager;

import ca.bcit.beproductiv.Tabs.TimerFragment;
import ca.bcit.beproductiv.Tabs.TodoFragment;

public class HomeActivity extends AppCompatActivity {
    private MaterialViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
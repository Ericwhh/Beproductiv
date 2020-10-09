package ca.bcit.beproductiv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


    }

    public void startSetUpWizard(View view) {
        Intent intent = new Intent(Settings.this, SettingsWizardActivity.class);
        startActivity(intent);
    }
}
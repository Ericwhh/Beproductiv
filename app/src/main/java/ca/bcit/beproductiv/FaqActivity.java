package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Button settingsButton = findViewById(R.id.faq_back_button);
        settingsButton.setOnClickListener(v -> finish());
    }
}
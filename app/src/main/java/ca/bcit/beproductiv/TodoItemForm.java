package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TodoItemForm extends AppCompatActivity {

    public enum FormAction {Add, Edit};
    FormAction FORM_ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_form);

        //String formActionString = getIntent().getStringExtra("FORM_ACTION");
        //FORM_ACTION = parseFormAction(formActionString);

        Button backButton = findViewById(R.id.btnBackFromForm);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    FormAction parseFormAction(String formActionString) {
        if (formActionString.equals("ADD"))
            return FormAction.Add;
        return FormAction.Edit;
    }
}
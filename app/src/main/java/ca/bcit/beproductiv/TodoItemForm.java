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
        Button saveButton = findViewById(R.id.todo_form_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button backButton = findViewById(R.id.todo_form_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.todo_form_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

    }

    FormAction parseFormAction(String formActionString) {
        if (formActionString.equals("ADD"))
            return FormAction.Add;
        return FormAction.Edit;
    }
}
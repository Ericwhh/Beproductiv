package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.Async.AddTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.GetTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.RemoveTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.UpdateTodoItemsAsync;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.Database.TodoItemDao;

public class TodoItemForm extends AppCompatActivity {

    int DEFAULT_TODO_UID = -1;

    int todoUID = DEFAULT_TODO_UID;
    String todoName = "";
    String todoDescription = "";

    Button btnSaveTodo;
    Button btnDeleteTodo;
    Button btnBack;

    TextInputEditText etTodoName;
    TextInputEditText etTodoDescription;

    public enum FormAction {Add, Edit};

    FormAction FORM_ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_form);

        btnSaveTodo = findViewById(R.id.todo_form_save_button);
        btnDeleteTodo = findViewById(R.id.todo_form_delete_button);
        btnBack = findViewById(R.id.todo_form_back_button);

        etTodoName = findViewById(R.id.etTodoName);
        etTodoDescription = findViewById(R.id.etTodoDescription);

        String formActionString = getIntent().getStringExtra("FORM_ACTION");
        FORM_ACTION = parseFormAction(formActionString);

        if (FORM_ACTION == FormAction.Edit) {
            todoUID = getIntent().getIntExtra("TODO_UID", DEFAULT_TODO_UID);
            todoName = getIntent().getStringExtra("TODO_NAME");
            todoDescription = getIntent().getStringExtra("TODO_DESCRIPTION");
        } else {
            btnDeleteTodo.setVisibility(View.GONE);
        }

        etTodoName.setText(todoName);
        etTodoDescription.setText(todoDescription);

        btnSaveTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoName = etTodoName.getText().toString();
                String todoDescription = etTodoDescription.getText().toString();

                if (!todoName.trim().isEmpty() && !todoDescription.trim().isEmpty()) {
                    TodoItem item = new TodoItem(todoName, todoDescription);
                    if (todoUID == DEFAULT_TODO_UID) {
                        new AddTodoItemsAsync(getApplicationContext()).execute(item);
                    } else {
                        item.setUID(todoUID);
                        new UpdateTodoItemsAsync(getApplicationContext()).execute(item);
                    }
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to add Todo Item",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDeleteTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveTodoItemsAsync(getApplicationContext()).execute(todoUID);
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
package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import ca.bcit.beproductiv.Database.Async.AddTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.RemoveTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.UpdateTodoItemsAsync;
import ca.bcit.beproductiv.Database.TodoItem;

public class TodoItemForm extends AppCompatActivity {

    final int DEFAULT_TODO_UID = -1;

    int todoUID = DEFAULT_TODO_UID;
    String todoName = "";
    String todoDescription = "";

    Button btnSaveTodo;
    Button btnDeleteTodo;
    Button btnBack;

    TextInputEditText etTodoName;
    TextInputEditText etTodoDescription;

    TextView tvFormHeading;

    public enum FormAction {Add, Edit}

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

        tvFormHeading = findViewById(R.id.todo_form_header);

        String formActionString = getIntent().getStringExtra("FORM_ACTION");
        FORM_ACTION = parseFormAction(formActionString);

        if (FORM_ACTION == FormAction.Edit) {
            tvFormHeading.setText(R.string.label_edit_todo);

            todoUID = getIntent().getIntExtra("TODO_UID", DEFAULT_TODO_UID);
            todoName = getIntent().getStringExtra("TODO_NAME");
            todoDescription = getIntent().getStringExtra("TODO_DESCRIPTION");
        } else {
            tvFormHeading.setText(R.string.label_add_todo);

            btnDeleteTodo.setVisibility(View.GONE);
        }

        etTodoName.setText(todoName);
        etTodoDescription.setText(todoDescription);

        btnSaveTodo.setOnClickListener(view -> {
            String todoName = etTodoName.getText().toString();
            String todoDescription = etTodoDescription.getText().toString();

            if (todoName.trim().isEmpty() || todoDescription.trim().isEmpty()) return;

            TodoItem item = new TodoItem(todoName, todoDescription);

            if (FORM_ACTION == FormAction.Add) {
                new AddTodoItemsAsync(getApplicationContext()).execute(item);
            } else {
                item.setUID(todoUID);
                new UpdateTodoItemsAsync(getApplicationContext()).execute(item);
            }

            finish();
        });

        btnBack.setOnClickListener(v -> finish());

        btnDeleteTodo.setOnClickListener(v -> {
            btnDeleteTodo.setOnClickListener(null);
            new RemoveTodoItemsAsync(getApplicationContext()).execute(todoUID);
            finish();
        });

    }

    FormAction parseFormAction(String formActionString) {
        if (formActionString.equals("ADD"))
            return FormAction.Add;
        return FormAction.Edit;
    }
}
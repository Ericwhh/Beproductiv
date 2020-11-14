package ca.bcit.beproductiv.Tabs;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.R;
import ca.bcit.beproductiv.TodoItemForm;

public class TodoFragment extends Fragment {

    public TodoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_todo, container, false);
        RecyclerView contRecycler = root.findViewById(R.id.my_recycler);


        LiveData<List<TodoItem>> myTodoItems;

        try {
            myTodoItems = new GetTodoItemsAsync(getContext()).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            myTodoItems = null;
            e.printStackTrace();
        }

        final TodoCardsAdapter todoCardsAdapter = new TodoCardsAdapter(new ArrayList<TodoItem>());
        contRecycler.setAdapter(todoCardsAdapter);

        myTodoItems.observe(getViewLifecycleOwner(), new Observer<List<TodoItem>>() {
            @Override
            public void onChanged(List<TodoItem> todoItems) {
                System.out.println("List<TodoItems onChanged");
                todoCardsAdapter.setTodoItems(todoItems);
                todoCardsAdapter.notifyDataSetChanged();
            }
        });



        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        contRecycler.setLayoutManager(lm);

        FloatingActionButton button = root.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), TodoItemForm.class);
                i.putExtra("FORM_ACTION", "ADD");
                view.getContext().startActivity(i);
            }
        });

        return root;
    }

    static class GetTodoItemsAsync extends AsyncTask<Void, Void, LiveData<List<TodoItem>>> {
        private final WeakReference<Context> contextRef;

        public GetTodoItemsAsync(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected LiveData<List<TodoItem>> doInBackground(Void ...voids) {
            AppDatabase db = AppDatabase.getInstance(contextRef.get());
            return db.getTaskDao().getAll();
        }
    }

    static class TodoCardsAdapter extends RecyclerView.Adapter<TodoCardsAdapter.ViewHolder>
    {
        private ArrayList<TodoItem> _todoItems;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final MaterialCardView cardView;

            public ViewHolder(MaterialCardView v) {
                super(v);
                cardView = v;
            }
        }

        public TodoCardsAdapter(List<TodoItem> todoItems) {
            _todoItems = new ArrayList<>(todoItems);
        }

        public void setTodoItems(List<TodoItem> todoItems) {
            _todoItems = new ArrayList<>(todoItems);
        }

        @Override
        public int getItemCount() {
            return _todoItems.size();
        }

        @NonNull
        @Override
        public TodoCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MaterialCardView cv = (MaterialCardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_todo_card, parent, false);

            return new ViewHolder(cv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final MaterialCardView cardView = holder.cardView;

            TextView todoCardName = cardView.findViewById(R.id.todo_name);
            todoCardName.setText(_todoItems.get(position).name);

            TextView todoCardDescription = cardView.findViewById(R.id.todo_description);
            todoCardDescription.setText(_todoItems.get(position).description);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(cardView.getContext(), TodoItemForm.class);
                    i.putExtra("FORM_ACTION", "EDIT");

                    // TODO: Send only uid instead of plaintext name and description
                    i.putExtra("TODO_UID", _todoItems.get(position).uid);
                    i.putExtra("TODO_NAME", _todoItems.get(position).name);
                    i.putExtra("TODO_DESCRIPTION", _todoItems.get(position).description);

                    cardView.getContext().startActivity(i);
                }
            });
        }
    }
}



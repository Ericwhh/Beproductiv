package ca.bcit.beproductiv.Tabs;

import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.Async.GetTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.RemoveTodoItemsAsync;
import ca.bcit.beproductiv.Database.Async.SetTimerTodoAsync;
import ca.bcit.beproductiv.Database.Async.UpdateTodoItemsAsync;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.HomeActivity;
import ca.bcit.beproductiv.R;
import ca.bcit.beproductiv.TodoItemForm;

public class TodoFragment extends Fragment {

    private View root;
    private LiveData<List<TodoItem>> myTodoItems;
    private boolean showCompleted = true;
    private TodoCardsAdapter todoCardsAdapter;
    private AppDatabase appDatabase;

    public TodoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getInstance(getContext());
        myTodoItems = appDatabase.getTaskDao().getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_todo, container, false);
        RecyclerView contRecycler = root.findViewById(R.id.my_recycler);

        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        showCompleted = sharedConfig.getBoolean("show_completed", true);
        ViewPager viewPager = getActivity().findViewById(R.id.pager);
        todoCardsAdapter = new TodoCardsAdapter(new ArrayList<>(), contRecycler, viewPager);
        contRecycler.setAdapter(todoCardsAdapter);

        myTodoItems.observe(getViewLifecycleOwner(), new Observer<List<TodoItem>>() {
            @Override
            public void onChanged(List<TodoItem> todoItems) {
                updateTodoAdapter(todoItems);
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        boolean configShowCompleted = sharedConfig.getBoolean("show_completed", false);
        if (configShowCompleted != showCompleted) {
            showCompleted = configShowCompleted;
            updateTodoAdapter(appDatabase.getTaskDao().getAllOnce());
        }
    }

    private void updateTodoAdapter(List<TodoItem> todoItems) {
        Stream<TodoItem> todoItemStream = todoItems.stream()
                .sorted(Comparator.comparing(TodoItem::getIsComplete));

        if (!showCompleted)
            todoItemStream = todoItemStream.filter(todoItem -> !todoItem.isComplete);

        todoCardsAdapter.setTodoItems(todoItemStream.collect(Collectors.toList()));
        todoCardsAdapter.notifyDataSetChanged();
    }

    static class TodoCardsAdapter extends RecyclerView.Adapter<TodoCardsAdapter.ViewHolder>
    {
        private ArrayList<TodoItem> _todoItems;

        private int _expandedPosition;
        private final RecyclerView _rootRecyclerView;
        private final ViewPager _viewPager;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final MaterialCardView cardView;

            private final RelativeLayout layoutMiddle;
            private final RelativeLayout layoutBottom;
            private final TextView todoCardName;
            private final TextView todoCardDescription;
            private final MaterialButton btnContextMenu;
            private final ImageView imageViewExpandCollapse;
            private final MaterialButton btnStartTimer;
            private final MaterialButton btnCompleteTodo;

            public ViewHolder(MaterialCardView v) {
                super(v);
                cardView = v;

                layoutMiddle = cardView.findViewById(R.id.layoutMiddle);
                layoutBottom = cardView.findViewById(R.id.layoutBottom);
                todoCardName = cardView.findViewById(R.id.todo_name);
                todoCardDescription = cardView.findViewById(R.id.todo_description);
                btnContextMenu = cardView.findViewById(R.id.btnEditItem);
                imageViewExpandCollapse = cardView.findViewById(R.id.imageViewExpandCollapse);
                btnStartTimer = cardView.findViewById(R.id.btnStartTimer);
                btnCompleteTodo = cardView.findViewById(R.id.btnCompleteTodo);
            }
        }

        public TodoCardsAdapter(List<TodoItem> todoItems, RecyclerView recyclerView, ViewPager viewPager) {
            _todoItems = new ArrayList<>(todoItems);
            _expandedPosition = -1;
            _rootRecyclerView = recyclerView;
            _viewPager = viewPager;
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MaterialCardView cardView = holder.cardView;
            final boolean isExpanded = position == _expandedPosition;

            TodoItem todoItem = _todoItems.get(position);

            holder.todoCardName.setText(todoItem.name);
            holder.todoCardDescription.setText(todoItem.description);

            if (todoItem.isComplete) {
                cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.surface_secondary));
                Drawable completed = ContextCompat.getDrawable(cardView.getContext(), R.drawable.ic_baseline_check_24);
                holder.imageViewExpandCollapse.setImageDrawable(completed);
                holder.layoutBottom.setVisibility(View.GONE);
                holder.layoutMiddle.setVisibility(View.GONE);
                return;
            } else {
                cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.surface));
            }

            Drawable expandCollapseIcon;

            if (isExpanded) {
                holder.layoutBottom.setVisibility(View.VISIBLE);
                holder.layoutMiddle.setVisibility(View.VISIBLE);
                expandCollapseIcon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.ic_baseline_expand_less_24);
            } else {
                holder.layoutBottom.setVisibility(View.GONE);
                holder.layoutMiddle.setVisibility(View.GONE);
                expandCollapseIcon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.ic_baseline_expand_more_24);
            }

            holder.imageViewExpandCollapse.setImageDrawable(expandCollapseIcon);

            holder.btnContextMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(cardView.getContext(), TodoItemForm.class);
                    i.putExtra("FORM_ACTION", "EDIT");

                    // TODO: Send only uid instead of plaintext name and description
                    i.putExtra("TODO_UID", todoItem.uid);
                    i.putExtra("TODO_NAME", todoItem.name);
                    i.putExtra("TODO_DESCRIPTION", todoItem.description);

                    cardView.getContext().startActivity(i);
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _expandedPosition = isExpanded ? -1 : position;
                    TransitionManager.beginDelayedTransition(_rootRecyclerView);
                    notifyDataSetChanged();
                }
            });

            holder.btnStartTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SetTimerTodoAsync(holder.cardView.getContext()).execute(todoItem.uid);
                    _viewPager.setCurrentItem(0);
                }
            });

            holder.btnCompleteTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    todoItem.isComplete = true;
                    new UpdateTodoItemsAsync(holder.cardView.getContext()).execute(todoItem);
                    notifyDataSetChanged();
                }
            });
        }
    }
}



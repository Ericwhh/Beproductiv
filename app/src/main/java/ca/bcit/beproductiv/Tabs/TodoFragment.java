package ca.bcit.beproductiv.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.Async.SetTimerTodoAsync;
import ca.bcit.beproductiv.Database.Async.UpdateTodoItemsAsync;
import ca.bcit.beproductiv.Database.TodoItem;
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
        contRecycler.setItemAnimator(null);
        myTodoItems.observe(getViewLifecycleOwner(), this::updateTodoAdapter);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        contRecycler.setLayoutManager(lm);

        FloatingActionButton button = root.findViewById(R.id.fab);
        button.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), TodoItemForm.class);
            i.putExtra("FORM_ACTION", "ADD");
            view.getContext().startActivity(i);
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
            private final LinearLayout buttonRow;
            private final TextView todoCardName;
            private final TextView todoCardDescription;
            private final TextView todoCardCompletionStatus;
            private final MaterialButton btnContextMenu;
            private final ImageView imageViewExpandCollapse;
            private final MaterialButton btnStartTimer;
            private final MaterialButton btnCompleteTodo;

            public ViewHolder(MaterialCardView v) {
                super(v);
                cardView = v;

                layoutMiddle = cardView.findViewById(R.id.layoutMiddle);
                layoutBottom = cardView.findViewById(R.id.layoutBottom);
                buttonRow = cardView.findViewById(R.id.todoButtonRow);
                todoCardName = cardView.findViewById(R.id.todo_name);
                todoCardDescription = cardView.findViewById(R.id.todo_description);
                todoCardCompletionStatus = cardView.findViewById(R.id.todoCompletionStatus);
                btnContextMenu = cardView.findViewById(R.id.btnEditItem);
                imageViewExpandCollapse = cardView.findViewById(R.id.imageViewExpandCollapse);
                btnStartTimer = cardView.findViewById(R.id.btnStartTimer);
                btnCompleteTodo = cardView.findViewById(R.id.btnCompleteTodo);
            }
        }

        public TodoCardsAdapter(List<TodoItem> todoItems, RecyclerView recyclerView, ViewPager viewPager) {
            this.setHasStableIds(true);
            _todoItems = new ArrayList<>(todoItems);
            _expandedPosition = -1;
            _rootRecyclerView = recyclerView;
            _viewPager = viewPager;
        }

        public void setTodoItems(List<TodoItem> todoItems) {
            _todoItems = new ArrayList<>(todoItems);

        }

        @Override
        public long getItemId(int position) {
            return _todoItems.get(position).uid;
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
                holder.todoCardCompletionStatus.setTextColor(cardView.getResources().getColor(R.color.secondaryText));
                holder.buttonRow.setVisibility(View.GONE);
                holder.todoCardCompletionStatus.setText(R.string.completed);
            } else {
                holder.buttonRow.setVisibility(View.VISIBLE);
                holder.todoCardCompletionStatus.setText(R.string.to_do);
                holder.todoCardCompletionStatus.setTextColor(cardView.getResources().getColor(R.color.primary));
                cardView.setCardBackgroundColor(cardView.getResources().getColor(R.color.surface));
            }

            Drawable expandCollapseIcon;

            if (isExpanded) {
                holder.layoutBottom.setVisibility(View.VISIBLE);
                holder.layoutMiddle.setVisibility(View.VISIBLE);
                holder.btnContextMenu.setVisibility(View.VISIBLE);
                expandCollapseIcon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.ic_baseline_expand_less_24);
            } else {
                holder.layoutBottom.setVisibility(View.GONE);
                holder.layoutMiddle.setVisibility(View.GONE);
                holder.btnContextMenu.setVisibility(View.GONE);
                expandCollapseIcon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.ic_baseline_expand_more_24);
            }

            holder.imageViewExpandCollapse.setImageDrawable(expandCollapseIcon);

            holder.btnContextMenu.setOnClickListener(v -> {
                Intent i = new Intent(cardView.getContext(), TodoItemForm.class);
                i.putExtra("FORM_ACTION", "EDIT");

                i.putExtra("TODO_UID", todoItem.uid);
                i.putExtra("TODO_NAME", todoItem.name);
                i.putExtra("TODO_DESCRIPTION", todoItem.description);
                i.putExtra("TODO_COMPLETE", todoItem.isComplete);

                cardView.getContext().startActivity(i);
            });

            cardView.setOnClickListener(view -> {
                _expandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(_rootRecyclerView);
                notifyDataSetChanged();
            });

            holder.btnStartTimer.setOnClickListener(view -> {
                new SetTimerTodoAsync(holder.cardView.getContext()).execute(todoItem.uid);
                _viewPager.setCurrentItem(0);
            });

            holder.btnCompleteTodo.setOnClickListener(v -> {
                todoItem.isComplete = true;
                new UpdateTodoItemsAsync(holder.cardView.getContext()).execute(todoItem);
                notifyDataSetChanged();
            });
        }
    }
}



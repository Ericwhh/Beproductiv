package ca.bcit.beproductiv.Tabs;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todo, container, false);
        RecyclerView contRecycler = root.findViewById(R.id.my_recycler);

        ArrayList<TodoItem> dummyItems = TodoItem.getDummyData();

        String[] todoNames = new String[dummyItems.size()];
        String[] todoDescriptions = new String[dummyItems.size()];

        for (int i=0; i < dummyItems.size(); i++) {
            todoNames[i] = dummyItems.get(i).name;
            todoDescriptions[i] = dummyItems.get(i).description;
        }

        CaptionedImagesAdapter adapter = new CaptionedImagesAdapter(todoNames, todoDescriptions);
        contRecycler.setAdapter(adapter);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        contRecycler.setLayoutManager(lm);

        return root;
    }

    static class CaptionedImagesAdapter extends RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder>
    {
        private String[] todoNames;
        private String[] todoDescriptions;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private MaterialCardView cardView;

            public ViewHolder(MaterialCardView v) {
                super(v);
                cardView = v;
            }
        }

        public CaptionedImagesAdapter(String[] todoNames, String[] todoDescriptions) {
            this.todoNames = todoNames;
            this.todoDescriptions = todoDescriptions;
        }

        @Override
        public int getItemCount() {
            return todoNames.length;
        }

        @Override
        public CaptionedImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MaterialCardView cv = (MaterialCardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_todo_card, parent, false);

            return new ViewHolder(cv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final MaterialCardView cardView = holder.cardView;

            TextView todoCardName = cardView.findViewById(R.id.todo_name);
            todoCardName.setText(todoNames[position]);

            TextView todoCardDescription = cardView.findViewById(R.id.todo_description);
            todoCardDescription.setText(todoDescriptions[position]);
        }
    }
}



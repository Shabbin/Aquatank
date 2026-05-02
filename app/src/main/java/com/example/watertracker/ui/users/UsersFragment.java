package com.example.watertracker.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.R;
import com.example.watertracker.WaterIntakeDBHelper;

import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private WaterIntakeDBHelper dbHelper;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        dbHelper = new WaterIntakeDBHelper(requireContext());

        recyclerView = root.findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(false);

        loadUsersFromDatabase();

        return root;
    }

    private void loadUsersFromDatabase() {
        dbHelper.seedUsersIfEmpty();

        List<User> users = dbHelper.getAllUsers();

        userAdapter = new UserAdapter(users);
        recyclerView.setAdapter(userAdapter);
    }
}
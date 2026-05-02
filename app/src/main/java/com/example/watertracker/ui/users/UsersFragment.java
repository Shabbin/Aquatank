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

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = root.findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(false);

        userAdapter = new UserAdapter(getDummyUsers());
        recyclerView.setAdapter(userAdapter);

        return root;
    }

    private List<User> getDummyUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User("Ayesha Rahman", "ayesha@example.com", "Active", "AR"));
        users.add(new User("Tanvir Hasan", "tanvir@example.com", "Active", "TH"));
        users.add(new User("Nusrat Jahan", "nusrat@example.com", "Pending", "NJ"));
        users.add(new User("Mahmud Khan", "mahmud@example.com", "Inactive", "MK"));
        users.add(new User("Sadia Islam", "sadia@example.com", "Active", "SI"));

        return users;
    }
}
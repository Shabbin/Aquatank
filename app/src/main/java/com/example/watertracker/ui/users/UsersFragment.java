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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(false);

        UserAdapter adapter = new UserAdapter(getCommunityUsers());
        recyclerView.setAdapter(adapter);

        return root;
    }

    private List<User> getCommunityUsers() {
        List<User> users = new ArrayList<>();

        users.add(new User("Rahim", "R", 2.50, 2.50));   // 100%
        users.add(new User("Karim", "K", 1.90, 2.50));   // 76%
        users.add(new User("Aisha", "A", 1.45, 2.50));   // 58%
        users.add(new User("Nadia", "N", 2.10, 2.50));   // 84%
        users.add(new User("Sakib", "S", 1.05, 2.50));   // 42%

        return users;
    }
}
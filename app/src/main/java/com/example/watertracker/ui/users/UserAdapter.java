package com.example.watertracker.ui.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.R;

import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.textInitials.setText(user.getInitials());
        holder.textName.setText(user.getName());

        holder.textAmount.setText(String.format(
                Locale.getDefault(),
                "%.2f L / %.2f L",
                user.getCurrentLiters(),
                user.getGoalLiters()
        ));

        holder.textPercent.setText(user.getProgressPercent() + "%");
        holder.textBadge.setText(user.getBadgeText());
        holder.progressBar.setProgress(user.getProgressPercent());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textInitials;
        TextView textName;
        TextView textAmount;
        TextView textPercent;
        TextView textBadge;
        ProgressBar progressBar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            textInitials = itemView.findViewById(R.id.text_user_initials);
            textName = itemView.findViewById(R.id.text_user_name);
            textAmount = itemView.findViewById(R.id.text_user_amount);
            textPercent = itemView.findViewById(R.id.text_user_percent);
            textBadge = itemView.findViewById(R.id.text_user_badge);
            progressBar = itemView.findViewById(R.id.progress_user);
        }
    }
}
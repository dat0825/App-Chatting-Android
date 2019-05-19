package com.example.datvtd.chatting.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.ViewHolder> {

    public List<User> mCheckBoxes;
    public int countBoxChecked;

    public UserGroupAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.countBoxChecked = 0;
        this.mCheckBoxes = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_add_group, parent, false);
        return new UserGroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = this.mUsers.get(position);
        if(user.getImageURL() != null){
            if (user.getImageURL().equals("default")) {
                holder.avatarUser.setImageResource(R.mipmap.ic_launcher_round);
            } else {
                Glide.with(this.mContext).load(user.getImageURL()).into(holder.avatarUser);
            }
        }

        holder.checkBoxUser.setTextColor(Color.BLACK);
        holder.checkBoxUser.setText(user.getUser());

        holder.checkBoxUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxUser.isChecked()) {
                    mCheckBoxes.add(user);
                    countBoxChecked++;
                } else {
                    mCheckBoxes.remove(user);
                    countBoxChecked--;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            avatarUser = itemView.findViewById(R.id.avatar_users);
            checkBoxUser = itemView.findViewById(R.id.checkbox_user_group);
        }

        private CircleImageView avatarUser;
        private CheckBox checkBoxUser;
    }

    private Context mContext;
    private List<User> mUsers;
}

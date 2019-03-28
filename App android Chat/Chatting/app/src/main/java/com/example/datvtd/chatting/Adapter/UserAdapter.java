package com.example.datvtd.chatting.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.DialogConfirmActivity;
import com.example.datvtd.chatting.InfoGroupActivity;
import com.example.datvtd.chatting.MainActivity;
import com.example.datvtd.chatting.MessageActivity;
import com.example.datvtd.chatting.Model.Chat;
import com.example.datvtd.chatting.Model.Chatlist;
import com.example.datvtd.chatting.Model.GroupChat;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public boolean isGroup;
    public List<User> mUsers = new ArrayList<>();
    public static String color = "";

    public UserAdapter() {

    }

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = new ArrayList<>();
        this.mGroupChats = new ArrayList<>();
        this.mUsers = mUsers;
        this.isChat = isChat;
        isGroup = false;
    }

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat, String typeInfo) {
        this.mContext = mContext;
        this.mUsers = new ArrayList<>();
        this.mGroupChats = new ArrayList<>();
        this.mUsers = mUsers;
        this.isChat = isChat;
        isGroup = false;
        this.typeInfo = typeInfo;
    }

    public UserAdapter(Context mContext, List<GroupChat> mGroupChats, boolean isChat, boolean isGroup) {
        this.mContext = mContext;
        this.mUsers = new ArrayList<>();
        this.mGroupChats = new ArrayList<>();
        this.mGroupChats = mGroupChats;
        this.isChat = isChat;
        this.isGroup = isGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (isGroup == false) {  //set cho chat binh thuong
            final User user = mUsers.get(position);

            //set image_profile
            if (user.getImageURL().equals("default")) {
                holder.profileImageImageView.setImageResource(R.mipmap.ic_launcher_round);
            } else {
                Glide.with(this.mContext).load(user.getImageURL()).into(holder.profileImageImageView);
            }

            holder.usernameTextView.setText(user.getUser());

            //set icon status
            if (this.isChat) {
                if (user.getStatus().equals("online")) {
                    holder.statusOnImageView.setVisibility(View.VISIBLE);
                    holder.statusOffImageView.setVisibility(View.GONE);
                } else {
                    holder.statusOnImageView.setVisibility(View.GONE);
                    holder.statusOffImageView.setVisibility(View.VISIBLE);
                }
                //set view cho InfoGroupActivity
                if (!typeInfo.equals("true")) {
                    showLastMessage(user.getId(), holder.lastMessageTextView);
                }
                // set icon remove cho InforGroupActivity
                if (typeInfo.equals("true") && firebaseUser.getUid().equals(user.getId())) {
                    holder.iconRemove.setVisibility(View.GONE);
                    holder.iconRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeUserGroup(user.getId());
                        }
                    });
                }
                // neu la admin set show icon remove for everyone
                if (typeInfo.equals("true")) {
                    this.adminGroup = MessageActivity.bundle.getString("adminGroup");
                    if (firebaseUser.getUid().equals(this.adminGroup)) {
                        holder.iconRemove.setVisibility(View.GONE);
                        holder.iconRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeUserGroup(user.getId());
                            }
                        });
                    }
                }
            } else {
                holder.lastMessageTextView.setVisibility(View.GONE);
                holder.statusOnImageView.setVisibility(View.GONE);
                holder.statusOffImageView.setVisibility(View.GONE);
            }

            //message-click item to chat
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("ID", user.getId());
                    intent.putExtra("checkGroup", "false");
                    if (typeInfo.equals("true")) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    getColor(user.getId());
                    mContext.startActivity(intent);
                }
            });
        }

        if (isGroup == true) {
            //set cho group chat
            final GroupChat groupChat = mGroupChats.get(position);

            if (groupChat.getImageGroupURL().equals("default")) {
                holder.profileImageImageView.setImageResource(R.mipmap.ic_launcher_round);
            } else {
                Glide.with(this.mContext).load(groupChat.getImageGroupURL()).into(holder.profileImageImageView);
            }

            holder.usernameTextView.setText(groupChat.getNameGroup());

            if (isChat == true) {
                //set last message cho groupchat
                showLastMessageGroupChat(groupChat.getIdGroup(), holder.lastMessageTextView);
            }

            //message-click item to groupchat
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isGroup = true;
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("idGroup", groupChat.getIdGroup());
                    intent.putExtra("checkGroup", "true");
                    intent.putExtra("nameGroup", groupChat.getNameGroup());
                    intent.putExtra("adminGroup", groupChat.getAdmin());
                    intent.putExtra("color", groupChat.getColor());
                    color = groupChat.getColor();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isGroup == false) {
            Log.d("not_grouppp2", String.valueOf(mUsers.size()));
            return mUsers.size();
        }

        if (isGroup == true) {
            Log.d("groupppp2", String.valueOf(mGroupChats.size()));
            return mGroupChats.size();
        }

        return 0;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.text_username);
            profileImageImageView = itemView.findViewById(R.id.avatar_users);
            statusOnImageView = itemView.findViewById(R.id.status_on);
            statusOffImageView = itemView.findViewById(R.id.status_off);
            lastMessageTextView = itemView.findViewById(R.id.text_last_message);
            iconRemove = itemView.findViewById(R.id.ic_remove_member);
        }

        private TextView usernameTextView, lastMessageTextView;
        private ImageView profileImageImageView;
        private ImageView statusOnImageView, statusOffImageView;
        private ImageView iconRemove;
    }

    public void showLastMessage(final String userID, final TextView lastMessage) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (isGroup == false && chat.isTypeImage() == false) {
                        if (chat.getReceiver().equals(firebaseUser.getUid())
                                && chat.getSender().equals(userID)
                                || chat.getReceiver().equals(userID)
                                && chat.getSender().equals(firebaseUser.getUid())) {
                            UserAdapter.this.lastMessage = chat.getMessage();
                        }
                        if (chat.getReceiver().equals(firebaseUser.getUid())
                                && chat.getSender().equals(userID)
                                && chat.isIsseen() == false) {
                            lastMessage.setTypeface(null, Typeface.BOLD);
                            lastMessage.setTextColor(Color.BLACK);
                        } else {
                            if (chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userID)
                                    || chat.isIsseen() == true) {
                                lastMessage.setTypeface(null, Typeface.NORMAL);
                                lastMessage.setTextColor(Color.GRAY);
                            }
                        }
                    }
                }
                lastMessage.setText(UserAdapter.this.lastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showLastMessageGroupChat(final String userID, final TextView lastMessage) {
        this.lastMessage = "";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.isGroup == true && chat.isTypeImage() == false) {
                        if (chat.getReceiver().equals(userID)) {
                            UserAdapter.this.lastMessage = chat.getMessage();
                            lastMessage.setText(UserAdapter.this.lastMessage);
                        }
                        if (!chat.getSender().equals(firebaseUser.getUid()) && chat.isIsseen() == false) {
                            lastMessage.setTypeface(null, Typeface.BOLD);
                            lastMessage.setTextColor(Color.BLACK);
                        } else {
                            lastMessage.setTypeface(null, Typeface.NORMAL);
                            lastMessage.setTextColor(Color.GRAY);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeUserGroup(String idUserRemove) {
        Intent intent = new Intent(mContext, DialogConfirmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("idUserRemove", idUserRemove);
        mContext.startActivity(intent);
    }

    public void getColor(String idReceiver) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid()).child(idReceiver);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chatlist chatlist = dataSnapshot.getValue(Chatlist.class);
                if (chatlist == null) {
                    color = "default";
                } else {
                    color = chatlist.getColor();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Context mContext;
    private List<GroupChat> mGroupChats = new ArrayList<>();
    private String lastMessage = "";
    private String typeInfo = ""; // set view cho InfoGroupActivity
    private boolean isChat;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String adminGroup = "";
}

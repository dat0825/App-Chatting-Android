package com.example.datvtd.chatting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.Model.Chat;
import com.example.datvtd.chatting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageURL) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MESSAGE_SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        if (viewType == TYPE_MESSAGE_RECEIVE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        if (viewType == TYPE_IMAGE_CHAT_SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_image_send,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        if (viewType == TYPE_IMAGE_CHAT_RECEIVE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_image_receive,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        return new MessageAdapter.ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        final Chat chat = mChat.get(position);

        if (!chat.isTypeImage()) {
            holder.messageTextView.setText(chat.getMessage());
        } else {
            Glide.with(mContext).load(chat.getMessage()).into(holder.chatImageView);
        }
        if (position == this.mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.seenTextView.setText("Seen");
            } else holder.seenTextView.setText("Sended");
        } else {
            holder.seenTextView.setVisibility(View.GONE);
        }

        //set icon cua nguoi nhan trong layout chat
        if (imageURL.equals("default")) {
            holder.profileImageReceiver.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            Glide.with(this.mContext).load(this.imageURL).into(holder.profileImageReceiver);
        }

        holder.chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatImageActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("urlChatImage",chat.getMessage());
//                intent.putExtras(bundle);
                intent.putExtra("urlChatImage", chat.getMessage());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            this.profileImageReceiver = itemView.findViewById(R.id.profile_image_receiver); // ảnh user chat vs mình
            this.messageTextView = itemView.findViewById(R.id.text_message);
            this.seenTextView = itemView.findViewById(R.id.text_seen);
            this.chatImageView = itemView.findViewById(R.id.chat_image);
        }

        private CircleImageView profileImageReceiver;
        private TextView messageTextView, seenTextView;
        private ImageView chatImageView;
    }

    @Override
    public int getItemViewType(int position) {
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (this.mChat.get(position).getSender().equals(this.firebaseUser.getUid())
                && !this.mChat.get(position).isTypeImage()) {
            return TYPE_MESSAGE_SEND;
        }
        if (!this.mChat.get(position).getSender().equals(this.firebaseUser.getUid())
                && !this.mChat.get(position).isTypeImage()) {
            return TYPE_MESSAGE_RECEIVE;
        }
        if (this.mChat.get(position).getSender().equals(this.firebaseUser.getUid())
                && this.mChat.get(position).isTypeImage()) {
            return TYPE_IMAGE_CHAT_SEND;
        }
        if (!this.mChat.get(position).getSender().equals(this.firebaseUser.getUid())
                && this.mChat.get(position).isTypeImage()) {
            return TYPE_IMAGE_CHAT_RECEIVE;
        }
        return 5;
    }

    private Context mContext;
    private List<Chat> mChat;
    private String imageURL;
    private FirebaseUser firebaseUser;
    private static final int TYPE_MESSAGE_SEND = 0;
    private static final int TYPE_MESSAGE_RECEIVE = 1;
    private static final int TYPE_IMAGE_CHAT_SEND = 2;
    private static final int TYPE_IMAGE_CHAT_RECEIVE = 3;
}

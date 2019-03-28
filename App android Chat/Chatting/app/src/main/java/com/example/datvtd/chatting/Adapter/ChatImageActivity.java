package com.example.datvtd.chatting.Adapter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.R;

public class ChatImageActivity extends AppCompatActivity {

    protected ImageView showImageChat;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat_image);
        showImageChat = findViewById(R.id.show_imageChat);

        Intent intent = getIntent();
        String urlChatImage = intent.getStringExtra("urlChatImage");
        Glide.with(ChatImageActivity.this).load(urlChatImage).into(showImageChat);
    }
}

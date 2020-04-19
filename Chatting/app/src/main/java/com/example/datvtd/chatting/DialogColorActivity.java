package com.example.datvtd.chatting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.datvtd.chatting.Adapter.UserAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DialogColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color);

        this.colorDialogImage = findViewById(R.id.image_color_dialog);
        this.colorView = findViewById(R.id.color_view);
        this.changeColorButton = findViewById(R.id.b_ok_change_color);

        colorDialogImage.setDrawingCacheEnabled(true);
        colorDialogImage.buildDrawingCache(true);

        this.idGroup = MessageActivity.bundle.getString("idGroup");


        colorDialogImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    bitmap = colorDialogImage.getDrawingCache();

                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);

                    hexColorValue = "#" + Integer.toHexString(pixel);
                    colorView.setBackgroundColor(Color.parseColor(hexColorValue));
                }
                return true;
            }
        });

        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idGroup != null) {
                    reference = FirebaseDatabase.getInstance().getReference("ChatGroup")
                            .child(idGroup).child("color");
                    reference.setValue(hexColorValue);
                } else {
                    //set màu cho cả bên gửi và nhận giống nhau
                    reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(new MessageActivity().bundle.getString("idCurrentUser"))
                            .child(new MessageActivity().bundle.getString("ID"));
                    reference.child("color").setValue(hexColorValue);
                    reference.child("id").setValue(new MessageActivity().bundle.getString("ID")); // tránh tình trạng khi set màu xong thì trong chatlist sẽ k add giá trị id khi nhắn tin.

                    reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(new MessageActivity().bundle.getString("ID"))
                            .child(new MessageActivity().bundle.getString("idCurrentUser"));
                    reference.child("color").setValue(hexColorValue);
                    reference.child("id").setValue(new MessageActivity().bundle.getString("idCurrentUser"));

                }

//                MessageActivity.bundle.putString("color",hexColorValue); //update lại giá trị color trong lớp messageActivity
                UserAdapter userAdapter = new UserAdapter();
                userAdapter.color = hexColorValue;

                Intent intent = new Intent(DialogColorActivity.this, MessageActivity.class);
                intent.putExtra("ID", new MessageActivity().bundle.getString("ID"));
                intent.putExtra("idGroup", new MessageActivity().bundle.getString("idGroup"));
                intent.putExtra("nameGroup", new MessageActivity().bundle.getString("nameGroup"));
                intent.putExtra("adminGroup", new MessageActivity().bundle.getString("adminGroup"));
                intent.putExtra("checkGroup", new MessageActivity().bundle.getString("checkGroup"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private ImageView colorDialogImage;
    private View colorView;
    private Bitmap bitmap;
    private Button changeColorButton;
    private DatabaseReference reference;
    private String idGroup = "";
    private String hexColorValue = "";
}

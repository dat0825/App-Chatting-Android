package com.example.datvtd.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;


public class CallActivity extends AppCompatActivity {

    public static SinchClient sinchClient;
    public static com.sinch.android.rtc.calling.Call call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        declineButton = findViewById(R.id.b_decline);
        acceptButton = findViewById(R.id.b_accept);
        hangupButton = findViewById(R.id.b_hangup);
        backgroundLayout = findViewById(R.id.backgoundLayout);
        nameCallerTextView = findViewById(R.id.nameCaller);

        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        nameCaller = intent.getStringExtra("nameCaller");
        action = intent.getStringExtra("action");
        avatarCaller = intent.getStringExtra("avatarCaller");
        avatarReceiver = intent.getStringExtra("avatarReceiver");
        nameReceiver = intent.getStringExtra("nameReceiver");

        if (action == null) {
            //set background and nameCaller cho người nhận cuộc gọi
            setViewReceiver();
        } else {
            //set background cho người gọi điện
            setViewCaler();
            callUser();
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call != null) {
                    call.answer();
                    Toast.makeText(getApplicationContext(), "Call is started", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "nulll", Toast.LENGTH_LONG).show();
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call != null) {
                    call.hangup();
                    sinchClient.stopListeningOnActiveConnection();
                    sinchClient.terminate();
                }
            }
        });

        hangupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
                sinchClient.stopListeningOnActiveConnection();
            }
        });
    }

    public void callUser() {
        if (call == null) {
            call = sinchClient.getCallClient().callUser(receiver);
            call.addCallListener(new SinchCallListener());
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        }
    }

    public void setViewCaler() {
        // set view cho người gọi
        declineButton.setVisibility(View.INVISIBLE);
        acceptButton.setVisibility(View.INVISIBLE);
        if (avatarReceiver.equals("default")) {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.ic_launcher_background);
            backgroundLayout.setBackground(drawable);
        } else {
            new MyDownloader().execute(avatarReceiver);
        }
        nameCallerTextView.setText(nameReceiver);
    }

    public void setViewReceiver() {
        //set View cho người nhận
        hangupButton.setVisibility(View.INVISIBLE);
        if (avatarCaller.equals("default")) {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.ic_launcher_background);
            backgroundLayout.setBackground(drawable);
        } else {
            new MyDownloader().execute(avatarCaller);
        }
        nameCallerTextView.setText(nameCaller);
    }

    public static class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(final CallClient callClient, final com.sinch.android.rtc.calling.Call incomingCall) {
            call = incomingCall;
        }
    }

    public class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(getApplicationContext(), "Calling....", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            Toast.makeText(getApplicationContext(), "End Game!", Toast.LENGTH_LONG).show();
            call = null;
            endedCall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {

        }
    }

    public class MyDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Drawable drawable = new BitmapDrawable(bitmap);
            backgroundLayout.setBackground(drawable);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    private RelativeLayout backgroundLayout;
    private ImageView declineButton;
    private ImageView acceptButton;
    private ImageView hangupButton;
    private TextView nameCallerTextView;
    private String receiver;
    private String action;
    private String nameCaller;
    private String avatarCaller;
    private String avatarReceiver;
    private String nameReceiver;
}



package com.example.datvtd.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class CallActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        declineButton = findViewById(R.id.b_decline);
        acceptButton = findViewById(R.id.b_accept);
        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        caller = intent.getStringExtra("caller");
        action = intent.getStringExtra("action");

        if (caller == null && receiver == null) {
            caller = "H4EJ39XcVNYMXXR4fGvfhqi1nn82";
            receiver = "O1uT2YW7B9UsUOWYoo7kgjFnc053";
        }

        if (sinchClient == null) {
            callVoice(caller);
        } else {
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.answer();
                    Toast.makeText(getApplicationContext(), "Call is started", Toast.LENGTH_LONG).show();
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
        }
        Log.d("abcbbcas", receiver + " -- " + caller);

        if (action != null) {
            if (action.equals("call")) {
                try {
                    Thread.sleep(2000);
//                    callUser();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void callVoice(String caller) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(caller)
                .applicationKey("08c39146-6d90-41b2-8c1d-2e76a03ea62b")
                .applicationSecret("LLRk8KPO6katlsOjm6VRZw==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
//        sinchClient.setSupportManagedPush(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

//        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(){
//
//        });

//        sinchClient.getCallClient().callUser(idReceiver);
        Log.d("abcbbcas", "dcmmmmmm");
    }

    public void callUser() {
        if (call == null) {
            call = sinchClient.getCallClient().callUser(receiver);
            call.addCallListener(new SinchCallListener());
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
            openCallerDialog(call);
        }
    }

    public void openCallerDialog(final com.sinch.android.rtc.calling.Call call) {
        AlertDialog alertDialogCall = new AlertDialog.Builder(CallActivity.this).create();
        alertDialogCall.setTitle("ALERT");
        alertDialogCall.setMessage("Calling");
        alertDialogCall.setCancelable(false);
        alertDialogCall.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
                sinchClient.stopListeningOnActiveConnection();
//                sinchClient.terminate();
            }
        });

        alertDialogCall.show();
    }

    public class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(final CallClient callClient, final com.sinch.android.rtc.calling.Call incomingCall) {
            Log.d("abcbbcas", "input - bphone");
//             set dialog for call
//            acceptButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
            call = incomingCall;
//                    call.answer();
//                    Toast.makeText(getApplicationContext(), "Call is started", Toast.LENGTH_LONG).show();
//                }
//            });
//            AlertDialog alertDialog = new AlertDialog.Builder(CallActivity.this).create();
//            alertDialog.setTitle("Calling");
//            alertDialog.setCancelable(false);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    if (call != null) {
//                        call.hangup();
//                        sinchClient.stopListeningOnActiveConnection();
////                        sinchClient.terminate();
//                    }
//                }
//            });
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Pick", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    call = incomingCall;
//                    call.answer();
//                    Toast.makeText(getApplicationContext(), "Call is started", Toast.LENGTH_LONG).show();
//                }
//            });
//
//            alertDialog.show();
        }
    }

    private class SinchCallListener implements CallListener {

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
            Log.d("sad213", "notificationn");
        }
    }

    private Button declineButton;
    public Button acceptButton;
    private String receiver;
    public String caller;
    private String action;
    public static SinchClient sinchClient;
    public static com.sinch.android.rtc.calling.Call call;
}



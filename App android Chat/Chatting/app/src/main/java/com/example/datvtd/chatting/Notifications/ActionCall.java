package com.example.datvtd.chatting.Notifications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.datvtd.chatting.MessageActivity;

public class ActionCall extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new MessageActivity().callVoice();
        new MessageActivity().callUser();
//        new MessageActivity().openCallerDialog(new MessageActivity().call);
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

}

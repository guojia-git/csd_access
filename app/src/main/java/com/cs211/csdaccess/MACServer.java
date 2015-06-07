package com.cs211.csdaccess;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.List;

// BroadcastReceiver within Service Reference http://stackoverflow.com/questions/9092134/broadcast-receiver-within-a-service
// Service Reference http://www.vogella.com/tutorials/AndroidServices/article.html

public class MACServer extends Service {

    private String smsResponse;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Update the true mac
        smsResponse = intent.getStringExtra("true_mac");
        // Register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(SmsReceiver, filter);
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(SmsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private MACServer instance() {
        return this;
    }
    // A local broadcaset receiver that intercepts sms
    private BroadcastReceiver SmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();
            final String SMS_BUNDLE = "pdus";
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                List<String> smsMessages = new ArrayList<String>();
                List<String> smsAddresses = new ArrayList<String>();
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                    String smsBody = smsMessage.getMessageBody().toString();
                    String address = smsMessage.getOriginatingAddress();
                    smsMessages.add(smsBody);
                    smsAddresses.add(address);
                }
                for (int i = 0; i < smsMessages.size(); ++i) {
                    String smsMessage = smsMessages.get(i);
                    if (smsMessage.equals("Request MAC")) { // detecting a request
                        String address = smsAddresses.get(i);
                        if (smsResponse.length() != 0) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(address, null, smsResponse, null, null);
                        }
                        break;
                    } else if (smsMessage.length() == 17 && smsMessage.charAt(2) == ':') { // detecting a MAC
                        // Send intent to the main activity
                        String new_mac = smsMessage;
                        Intent it = new Intent("com.cs211.csdaccess");
                        it.putExtra("new_mac", new_mac);
                        LocalBroadcastManager.getInstance(instance()).sendBroadcast(it);
                    }

                }

                //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            }
        }
    };



}

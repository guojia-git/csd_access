package com.cs211.csdaccess;

/**
 * Created by Administrator on 2015/6/4.
 * Reference: http://javapapers.com/android/android-receive-sms-tutorial/
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    private static String smsResponse;
    private String newMac = "";
    private boolean receivedNewMac = false;
    private boolean isBuffering = false;

    public static final String SMS_BUNDLE = "pdus";
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        String mac = null;
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
                // if detect a request
                if (smsMessage.equals("Request MAC")) { // detecting a request
                    String address = smsAddresses.get(i);
                    if (smsResponse.length() != 0) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(address, null, smsResponse, null, null);
                    }
                    break;
                } else if (smsMessage.length() == 17 && smsMessage.charAt(2) == ':') { // detecting a MAC
                    receivedNewMac = false;
                    //this will update the UI with message
                    MainActivity inst = MainActivity.instance();
                    if (smsMessage != null)
                        inst.et_mac.setText(smsMessage);
                }

            }

            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();


        }
    }

    public String checkNewMac() {
        if (receivedNewMac) {
            receivedNewMac = false;
            return newMac;
        } else {
            return null;
        }
    }
}

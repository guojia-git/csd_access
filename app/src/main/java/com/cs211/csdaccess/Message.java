package com.cs211.csdaccess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Administrator on 2015/6/4.
 *
 *
 * // SMS send reference: http://javapapers.com/android/android-receive-sms-tutorial/
 */
public class Message {



    public String receiveSMS(Context context, Intent intent)
    {
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        String message = null;

        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,"senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
        return message;
    }
}

package com.family.locator.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.family.locator.DBHelper;
import com.family.locator.Services.LocationSendHelper;
import com.family.locator.utitlity.Constants;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class SmsBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TEST", "Hey this is reciever");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody().toUpperCase();

                String phoneNumber = smsMessage.getOriginatingAddress();

                DBHelper dbHelper = new DBHelper(context);
                ArrayList<String> list = new ArrayList<>();

                Map<Long,String> mapData = dbHelper.getData();
                for (Map.Entry<Long,String> entry: mapData.entrySet()) {
                    list.add(entry.getValue());
                }

                boolean isPresent = list.stream().anyMatch( obj -> (phoneNumber.contains(obj)) );

                if(isPresent) {
                    if (smsMessage != null && messageBody.contains(Constants.SEND_LOCATION_COMMAND.toUpperCase())) {
                        boolean sendCurrentLocation = messageBody.contains(Constants.SEND_CURRENT_LOCATION_COMMAND.toUpperCase());

                        Intent serviceIntent = new Intent(context,LocationSendHelper.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.FROM_PHONE_NUMBER,smsMessage.getOriginatingAddress());
                        bundle.putString(Constants.MESSAGE_CONTENT,messageBody);
                        bundle.putString(Constants.SEND_CURRENT_LOCATION_COMMAND,Boolean.toString(sendCurrentLocation));

                        serviceIntent.putExtras(bundle);
                        context.startService(serviceIntent);
                    }
                } else {
//                    Toast.makeText(context,"Not doing anything as number is not registered",Toast.LENGTH_SHORT).show();
                }


            }

        }
    }
}

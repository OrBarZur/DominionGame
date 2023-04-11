/**
 * PhoneCallReceiver is a broadcast receiver that listens to call state changes.
 * When the phone is ringing, it automatically hangs up and sends SMS to this number phone.
 */
package com.example.dominion_game.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import static android.content.Context.TELEPHONY_SERVICE;

public class PhoneCallReceiver extends BroadcastReceiver {

    /**
     * A function that is called when the BroadcastReceiver is receiving an Intent broadcast.
     * @param context The Context in which the receiver is running (GameActivity)
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new PhoneStateListener() {
            /**
             * A function that is called when there is a change in the call state.
             * @param state The state code that changed to, for example 1 is CALL_STATE_RINGING
             * @param incomingNumber A String with the phone number
             */
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    // ends call only if the SDK version is LOLLIPOP or above
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                        if (tm != null)
                            tm.endCall();
                    }
                    // creates a SmsManager that sends the message
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(incomingNumber, null,
                            "Sorry, I will call you back later because I am playing Dominion right now.", null, null);
                    Toast.makeText(context, "Message Sent to " + incomingNumber, Toast.LENGTH_LONG).show();
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}

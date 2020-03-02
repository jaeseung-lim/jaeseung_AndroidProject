package org.androidtown.sns_project.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import org.androidtown.sns_project.R;
import org.androidtown.sns_project.activity.ChatActivity;
import org.androidtown.sns_project.activity.MainActivity;

//import androidx.media.app.NotificationCompat;
//import androidx.core.app.NotificaionCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.v(TAG, "onMessageReceived 함수 시작 ");

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages.
        // Data messages are handled here in onMessageReceived whether the app is in the foreground or background.
        // Data messages are the type traditionally used with GCM.
        // Notification messages are only received here in onMessageReceived when the app is in the foreground.
        // When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app.
        // Messages containing both notification and data payloads are treated as notification messages.
        // The Firebase console always sends notification messages.
        // For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        //get current user from shared preferences
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID","None" );

        Log.v(TAG, "onMessageReceived 함수의 savedCurrentUser : "+savedCurrentUser);

        String sent = remoteMessage.getData().get("sent");
        Log.v(TAG, "onMessageReceived 함수의 sent : "+sent);
        String user = remoteMessage.getData().get("user");
        Log.v(TAG, "onMessageReceived 함수의 user : "+user);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert sent != null;
        if(firebaseUser!=null && sent.equals(firebaseUser.getUid())){

            if(!savedCurrentUser.equals(user)){

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

                    Log.v(TAG, "onMessageReceived 함수의 sendOAndAboveNotification ");
                    sendOAndAboveNotification(remoteMessage);

                }else{

                    Log.v(TAG, "onMessageReceived 함수의 sendNormalNotification ");
                    sendNormalNotification(remoteMessage);

                }

            }

        }


        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
       /* if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }*/

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {

        Log.v(TAG, "sendNormalNotification 함수 시작 ");

        String channelId ="channel_id";

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.v(TAG, "sendNormalNotification 함수 - user " + user);
        Log.v(TAG, "sendNormalNotification 함수 - icon "+ icon);
        Log.v(TAG, "sendNormalNotification 함수 - title "+title);
        Log.v(TAG, "sendNormalNotification 함수 - body "+ body);


        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int i = Integer.parseInt(user.replaceAll("[\\D]","")); // request conde
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("memberUid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        //실제 사용자에게 UI를 그리라고 알려주는(notify) 역할을 한다.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if(i>0){
            j=i;
        }
        notificationManager.notify(j,notificationBuilder.build());


        /////////////////////////////////////////////////////////////////////////////
        //Intent intent = new Intent(this, .class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);
        // Since android Oreo notification channel is needed.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/
        /////////////////////////////////////////////////////////////////////////////

    }


    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {

        Log.v(TAG, "sendOAndAboveNotification 함수 시작 ");

        //String channelId ="channel_id";

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.v(TAG, "sendOAndAboveNotification 함수 user " + user);
        Log.v(TAG, "sendOAndAboveNotification 함수 icon " + icon);
        Log.v(TAG, "sendOAndAboveNotification 함수 title "+ title);
        Log.v(TAG, "sendOAndAboveNotification 함수 body "+body);

        //실제 사용자에게 UI를 그리라고 알려주는(notify) 역할을 한다.
        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //오레오 이상을 target하는 앱이라면 필수적으로 구현해야하는 푸시 전용 채널이며, 이 채널은 그룹핑할 수 있어 사용자가 직접 푸시 그룹들로 묶인 채널을 받을지 안받을지 제어할 수 있도록 하는 역할이다.
        //NotificationChannel notificationChannel = new NotificationChannel(channelId, "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
        //assert notificationManager != null;
        //notificationManager.createNotificationChannel(notificationChannel);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]","")); // request conde
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("memberUid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotification(title,body,pendingIntent,defaultSoundUri,icon);


        int j = 0;
        if(i>0){
            j=i;
        }
        notification1.getManger().notify(j,builder.build());
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
        Log.v(TAG, "onNewToken : " + s);
    }

    /*@Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken(, );
        if(user!=null){
            updateToken(tokenRefresh);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        reference.child(user.getUid()).setValue(token);
    }*/
    // [END receive_message]


    // [START on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    /*private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
   /* private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }*/

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    /*@Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }*/
    // [END on_new_token]



    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    /*private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }*/



    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    /*private void sendNotification(String messageBody) {
    }*/




}

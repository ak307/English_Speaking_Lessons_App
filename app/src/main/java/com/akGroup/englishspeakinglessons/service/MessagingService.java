package com.akGroup.englishspeakinglessons.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.api.activity.ChatWithFriendActivity;
import com.akGroup.englishspeakinglessons.api.activity.VideoCallIncomingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    private Utils utils = new Utils();
    private final String CHANNEL_ID = "1000";


    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {

        // for normal message
        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
            if (type == null){
                Map<String, String> map = remoteMessage.getData();
                String senderUserName = map.get("senderUserName");
                String message = map.get("message");
                String receiverID = map.get("receiverID");
                String receiverName = map.get("receiverName");
                String receiverImage = map.get("receiverImage");
                String chatID = map.get("chatID");
                String senderId = map.get("senderId");
                String senderProfileImg = map.get("senderProfileImg");


                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                    createOreoNotification(senderUserName, message,
                            senderId, senderProfileImg, chatID, receiverID, receiverName, receiverImage);
                }
                else  {
                    createNormalNotification(senderUserName, message,
                            senderId, senderProfileImg, chatID, receiverID, receiverName, receiverImage);
                }
            }
        }



        // for video call
        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);


        if (type != null){
            if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                Intent intent = new Intent(getApplicationContext(), VideoCallIncomingActivity.class);
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
                intent.putExtra(Constants.CALLER_NAME,
                        remoteMessage.getData().get(Constants.CALLER_NAME));
                intent.putExtra(Constants.CALLER_IMAGE,
                        remoteMessage.getData().get(Constants.CALLER_IMAGE));
                intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN));
                intent.putExtra(Constants.REMOTE_MSG_CALL_ROOM,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_CALL_ROOM));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE));

                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
            }
        }


        super.onMessageReceived(remoteMessage);

    }


    private static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }



    public void getToken(){
        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                if (task.isSuccessful()){
                    onNewToken(task.getResult());
                }
            }
        });
    }


    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        updateToken(s);
        super.onNewToken(s);
    }




    private void updateToken(String token){
        DocumentReference documentReference = FirebaseFirestore
                .getInstance()
                .collection("user_profile_info")
                .document(utils.getUserID());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    // successfully updated token
                }
            }
        });
    }





    private void createNormalNotification(String senderUserName, String message,
                                          String senderId, String senderImageStr,
                                          String chatID, String receiverID,
                                          String receiverName, String receiverImageUrl){

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(senderUserName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null))
                .setSound(uri);


        Intent intent = new Intent(this, ChatWithFriendActivity.class);
        intent.putExtra("groupCollectionId", chatID);
        intent.putExtra("uid", senderId);
        intent.putExtra("friendUsername", senderUserName);
        intent.putExtra("friendImageUrl", senderImageStr);
        intent.putExtra("myName", receiverName);
        intent.putExtra("myUid", receiverID);
        intent.putExtra("myImageUrl", receiverImageUrl);



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85-65), builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotification(String senderUserName, String message,
                                        String senderId, String senderImageStr,
                                        String chatID, String receiverID,
                                        String receiverName, String receiverImageUrl){

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Message",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Message Description");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, ChatWithFriendActivity.class);
        intent.putExtra("groupCollectionId", chatID);
        intent.putExtra("uid", senderId);
        intent.putExtra("friendUsername", senderUserName);
        intent.putExtra("friendImageUrl", senderImageStr);
        intent.putExtra("myName", receiverName);
        intent.putExtra("myUid", receiverID);
        intent.putExtra("myImageUrl", receiverImageUrl);









        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(senderUserName)
                .setContentText(message)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        manager.notify(new Random(85-65).nextInt(), notification);
    }





}

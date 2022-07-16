package com.akGroup.englishspeakinglessons.service;

import static com.akGroup.englishspeakinglessons.service.Constants.REMOTE_MSG_INVITATION;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class ChatService {
    private final String NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send";
    private Context context;


    public ChatService(Context context) {
        this.context = context;
    }




    public void getTokenAndSendNotification(String myUserName, String message,
                                            String  receiverID, String receiverImage, String receiverName,
                                            String chatID, String senderId,
                                            String senderProfileImg, String serverKey){
        DocumentReference documentReference = FirebaseFirestore
                .getInstance()
                .collection("user_profile_info")
                .document(receiverID);

        documentReference
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    String token = (String) task.getResult().get("token");

                    JSONObject to = new JSONObject();
                    JSONObject data = new JSONObject();

                    try {
                        data.put("senderUserName", myUserName);
                        data.put("message", message);
                        data.put("receiverID", receiverID);
                        data.put("receiverName", receiverName);
                        data.put("chatID", chatID);
                        data.put("receiverImage", receiverImage);
                        data.put("senderId", senderId);
                        data.put("senderProfileImg", senderProfileImg);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        to.put("to", token);
                        to.put("data", data);

                        sendNotification(to, Constants.CHAT_TYPE, serverKey);

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    public void getTokenAndSendNotificationForVideoCall(String receiverToken, String invitorToken,
                                                        String meetingType, String callerName,
                                                        String callerImage, String callRoomStr,
                                                        String type, String serverKey){

        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.CALLER_NAME, callerName);
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, invitorToken);
            data.put(Constants.CALLER_IMAGE, callerImage);

            data.put(Constants.REMOTE_MSG_CALL_ROOM, callRoomStr);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            to.put("to", receiverToken);
            to.put("data", data);

            sendNotification(to, type, serverKey);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }






    public void sendNotification(JSONObject to, String type, String serverKey){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NOTIFICATION_URL, to, response -> {
            Log.d("notification", "sendNotification: " + response);
        }, error-> {
            Log.d("notification", "sendNotification: " + error);


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + serverKey);
                map.put("Content-Type", "application/json");

                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }
}

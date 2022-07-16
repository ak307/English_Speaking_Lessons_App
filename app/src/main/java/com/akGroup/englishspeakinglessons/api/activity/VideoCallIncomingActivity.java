package com.akGroup.englishspeakinglessons.api.activity;

import static com.akGroup.englishspeakinglessons.service.Constants.REMOTE_MSG_INVITATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.service.ChatService;
import com.akGroup.englishspeakinglessons.service.Constants;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VideoCallIncomingActivity extends AppCompatActivity {
    private String SERVER_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_incoming);


        ImageView callerImageView = findViewById(R.id.callerImageView);
        TextView callerName = findViewById(R.id.callerName);
        ImageView callTypeImageView = findViewById(R.id.imageMeetingTypeForIncoming);
        FloatingActionButton acceptCallBtn = findViewById(R.id.acceptCall);
        FloatingActionButton declineCallBtn = findViewById(R.id.declineCall);


        if (getCallType() != null){
            if (getCallImage() != null && !getCallImage().equals("")) {
                Glide.with(getApplicationContext())
                        .load(Uri.parse(getCallImage()))
                        .into(callerImageView);
            }

            callerName.setText(getCallerName());


            if (getCallType().equals("video")){
                callTypeImageView.setImageResource(R.drawable.ic_videocam_24);
            }
            else {
                callTypeImageView.setImageResource(R.drawable.ic_call_24);
            }

        }

        getServerKey();


        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
            }
        });


        declineCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
                finish();
            }
        });
    }



    private void sendInvitationResponse(String type, String receiverToken){
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            to.put("to", receiverToken);
            to.put("data", data);


            sendNotification(to, type);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void sendNotification(JSONObject to, String type){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.NOTIFICATION_URL, to, response -> {
            if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                try {
                    URL serverUrl = new URL("https://meet.jit.si");
                    JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                    builder.setServerURL(serverUrl);
                    builder.setWelcomePageEnabled(false);
                    builder.setFeatureFlag("invite.enable", false);
                    builder.setFeatureFlag("meeting-name.enabled", false);
                    builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_CALL_ROOM));

                    if (getCallType().equals("audio")){
                        builder.setVideoMuted(true);
                    }



                    JitsiMeetActivity.launch(VideoCallIncomingActivity.this, builder.build());
                    finish();

                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }, error-> {
            Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            finish();

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }




    public void getServerKey(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("cloud_messaging_server_key")
                .document()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                SERVER_KEY = task.getResult().getString("server_key");
                            }
                        }
                    }
                });
    }




    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(getApplicationContext(), "invitation cancel", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "type is null.", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    private String getCallType(){
        return getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
    }

    private String getCallerName(){
        return getIntent().getStringExtra(Constants.CALLER_NAME);
    }

    private String getCallImage(){
        return getIntent().getStringExtra(Constants.CALLER_IMAGE);
    }
}
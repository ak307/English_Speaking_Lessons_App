package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.service.ChatService;
import com.akGroup.englishspeakinglessons.service.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.UUID;


public class VideoCallOutGoingActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView tvName, tvProf;
    private FloatingActionButton declineBtn;
    private ImageView imageMeetingType;
    private FirebaseFirestore firebaseFirestore;
    private String callRoom = null;
    private Utils utils;




    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_out_going);


        imageMeetingType = findViewById(R.id.imageMeetingTypeForOutgoing);
        imageView = findViewById(R.id.iv_og_vc);
        tvName = findViewById(R.id.name_vc_og);
        tvProf = findViewById(R.id.prof_og_vc);
        declineBtn = findViewById(R.id.decline_vc_og);


        firebaseFirestore = FirebaseFirestore.getInstance();
        utils = new Utils();
        chatService = new ChatService(getApplicationContext());


        tvName.setText(getReceiverUserName());
        if (getInvitationType() != null){
            if (getInvitationType().equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_videocam_24);
            }
            else {
                imageMeetingType.setImageResource(R.drawable.ic_call_24);
            }
        }


        if (!getReceiverProfileImgUrl().equals("")) {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(getReceiverProfileImgUrl()))
                    .into(imageView);
        }


        if (getInvitationType() != null && getMyName() != null)
            initiateCall();


        declineBtnClicked();


    }




//    private void checkAndCreateVideoCallRoom(){
//        firebaseFirestore
//                .collection("videoCallRoom")
//                .whereArrayContains("members", Arrays.asList(utils.getUserID()))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            List<DocumentSnapshot> docs = task.getResult().getDocuments();
//
//                            if (!docs.isEmpty()){
//                                int count = 0;
//
//                                for (DocumentSnapshot doc : docs){
//                                    String groupId = doc.getString("docId");
//                                    ArrayList<String> members = (ArrayList<String>) doc.get("members");
//
//                                    if (members.contains(getReceiverId())){
//
//
//                                        // do something no need to create new one
//
//
//                                        break;
//                                    }
//                                    count++;
//                                }
//
//                                if (docs.size() == count){
//                                    createVideoCallRoomCollectionInFirestore(position);
//                                }
//
//                            }
//                            else
//                                createVideoCallRoomCollectionInFirestore(position);
//
//                        }
//                    }
//                });
//    }
//
//
//    private void createVideoCallRoomCollectionInFirestore(int position){
//        ArrayList<String> members = new ArrayList<>();
//        members.add(utils.getUserID());
//        members.add(getReceiverId());
//
//        String docId = firebaseFirestore
//                .collection("videoCallRoom")
//                .document().getId();
//
//
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("createdAt", FieldValue.serverTimestamp());
//        map.put("createdBy", utils.getUserID());
//        map.put("docId", docId);
//        map.put("members", members);
//        map.put("type", 1);
//
//
//        firebaseFirestore
//                .collection("videoCallRoom")
//                .document(docId)
//                .set(map)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            navigateToChatActivity(position, docId);
//                        }
//                    }
//                });
//    }



//    private void getInviterToken(){
//        FirebaseMessaging.getInstance()
//                .getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (task.isSuccessful()){
//                            inviterToken = task.getResult();
//                            initiateMeeting(getInvitationType(), getFriendToken());
//
//                        }
//                    }
//                });
//    }



//    private void initiateMeeting(String meetingType, String receiverToken){
//        try {
//            JSONArray tokens = new JSONArray();
//            tokens.put(receiverToken);
//
//            JSONObject body = new JSONObject();
//            JSONObject data = new JSONObject();
//
//
//            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION);
//            data.put(REMOTE_MSG_MEETING_TYPE, meetingType);
//            data.put(USER_NAME, getReceiverUserName());
//            data.put(REMOTE_MSG_INVITER_TOKEN, getMyToken());
//
//            body.put(REMOTE_MSG_DATA, data);
//            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);
//
//            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION);
//
//        }
//        catch (Exception exception){
//
//            Toast.makeText(VideoCallOutGoingActivity.this, "LLL: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//






    private void initiateCall(){
        callRoom = getMyName() + "_" + UUID.randomUUID().toString().substring(0, 5);



        chatService.getTokenAndSendNotificationForVideoCall(getFriendToken(), getMyToken(), getInvitationType(),
                    getMyName(), getMyImageUrl(), callRoom, Constants.REMOTE_MSG_INVITATION, getserverKey());

    }



    private void declineBtnClicked(){
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelInvitation(getFriendToken());
                onBackPressed();
            }
        });
    }





    private void cancelInvitation(String receiverToken){
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);


        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            to.put("to", receiverToken);
            to.put("data", data);


            chatService.sendNotification(to, Constants.REMOTE_MSG_INVITATION_CANCELLED, getserverKey());

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                    try {
                        URL serverUrl = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverUrl);
                        builder.setWelcomePageEnabled(false);
                        builder.setFeatureFlag("invite.enabled", false);
                        builder.setFeatureFlag("meeting-name.enabled", false);
                        builder.setRoom(callRoom);

                        if (getInvitationType().equals("audio")){
                            builder.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(VideoCallOutGoingActivity.this, builder.build());
                        finish();

                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(getApplicationContext(), "invitation rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "type is null.", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver, new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }



    private String getReceiverProfileImgUrl(){
        return getIntent().getStringExtra("receiverProfileImg");
    }


    private String getReceiverId(){
        return getIntent().getStringExtra("receiverUid");
    }


    private String getReceiverUserName(){
        return getIntent().getStringExtra("receiverName");
    }


    private String getInvitationType(){
        return getIntent().getStringExtra("type");
    }


    private String getFriendToken(){
        return getIntent().getStringExtra("token");
    }

    private String getMyToken(){
        return getIntent().getStringExtra("myToken");
    }


    private String getMyName(){
        return getIntent().getStringExtra("myName");
    }

    private String getMyImageUrl(){
        return getIntent().getStringExtra("myImageUrl");
    }

    private String getserverKey(){
        return getIntent().getStringExtra("serverKey");
    }


}
package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        EditText editText = (EditText) findViewById(R.id.reportEditText);
        Button button = (Button) findViewById(R.id.reportSubmitBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        utils = new Utils();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportText = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(reportText)){
                    addReportToFirebase(reportText);
                }
            }
        });
    }


    private void addReportToFirebase(String desc){

        String docId = firebaseFirestore.collection("reports")
                .document()
                .getId();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("docID", docId);
        hashMap.put("reportDesc", desc);
        hashMap.put("reportBy", utils.getUserID());
        hashMap.put("reportedDocId", getReportedDocId());
        hashMap.put("reportedAt", FieldValue.serverTimestamp());

        firebaseFirestore.collection("reports")
                .document(docId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ReportActivity.this, "Reported successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });

    }


    // if you want to go back to previous fragment from an activity call this
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private String getReportedDocId(){
        return getIntent().getStringExtra("reportedDocId");
    }



}
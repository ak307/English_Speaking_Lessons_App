package com.akGroup.englishspeakinglessons.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akGroup.englishspeakinglessons.R;
import com.akGroup.englishspeakinglessons.Utils.Utils;
import com.akGroup.englishspeakinglessons.databaseHelper.SavedPracticeAudioDatabaseHelper;
import com.akGroup.englishspeakinglessons.model.PracticeConversationSaved;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SavedPracticeConversationListviewAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<PracticeConversationSaved> conversationArrayList;
    private SavedPracticeAudioDatabaseHelper savedPracticeAudioDatabaseHelper;

    public SavedPracticeConversationListviewAdapter(Context context, ArrayList<PracticeConversationSaved> conversationArrayList) {
        this.context = context;
        this.conversationArrayList = conversationArrayList;
    }



    @Override
    public int getCount() {
        return conversationArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.practice_saved_listview_item_layout, parent, false);

        }

        ImageButton saveMenuImgBtn = (ImageButton) convertView.findViewById(R.id.savedMenuImgBtn);
        TextView title = (TextView) convertView.findViewById(R.id.savedPracticeAudioTitle);
        TextView date = (TextView) convertView.findViewById(R.id.savedPracticeAudioDate);

        savedPracticeAudioDatabaseHelper = new SavedPracticeAudioDatabaseHelper(context);


        setMenuBtn(saveMenuImgBtn, convertView, position);
        title.setText("Practice with " + conversationArrayList.get(position).getConversationWith());
        date.setText(conversationArrayList.get(position).getDocID());



        return convertView;
    }







    private void setMenuBtn(ImageButton menuBtn, View view, int position){
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                showPopupMenu(menuBtn, position);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPopupMenu(ImageButton menuBtn, int position) {
        PopupMenu popup = new PopupMenu(context, menuBtn);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.delete_item_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.savedDeleteMenuBtn) {
//                    deleteAudioFromFirebaseStorage(conversationArrayList.get(position).getSavedConversationUrl(),
//                            conversationArrayList.get(position).getDocID(), position);

                    savedPracticeAudioDatabaseHelper.deleteData(conversationArrayList.get(position).getSavedConversationUrl());
                    conversationArrayList.remove(position);

                    return true;

                }
                return false;
            }
        });

        popup.setForceShowIcon(true);
        popup.setGravity(Gravity.END);
        popup.show();
    }




    private void deleteAudioFromFirebaseStorage(String url, String docID, int position) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                notifyDataSetChanged();
                deletePracticeConversationFromFirestore(docID, position);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context, "Delete Failed", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deletePracticeConversationFromFirestore(String docID, int position){
        Utils utils = new Utils();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore
                .collection("user_profile_info")
                .document(utils.getUserID())
                .collection("savedPracticeConversations")
                .document(docID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
//                            conversationArrayList.remove(position);
                        }
                    }
                });



    }



}

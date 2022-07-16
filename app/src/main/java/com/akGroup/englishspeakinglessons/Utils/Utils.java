package com.akGroup.englishspeakinglessons.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Utils {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public String getUserID(){
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null) {
            return firebaseUser.getUid();
        }
        return null;
    }

    public boolean isLogin(){
        return getUserID() != null;
    }


    public void hideKeyboard(@NonNull Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

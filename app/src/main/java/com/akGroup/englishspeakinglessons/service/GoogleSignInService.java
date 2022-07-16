package com.akGroup.englishspeakinglessons.service;

import android.app.Dialog;
import android.content.Context;

import com.akGroup.englishspeakinglessons.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleSignInService {
    private Context context;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLEAUTH";
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    Dialog dialog;

    public GoogleSignInService(Context context) {
        this.context = context;
    }

    public void ConfigureGoogleSignIn(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    }





}

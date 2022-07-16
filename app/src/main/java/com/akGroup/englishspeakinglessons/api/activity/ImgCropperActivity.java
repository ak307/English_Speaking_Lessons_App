package com.akGroup.englishspeakinglessons.api.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.akGroup.englishspeakinglessons.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ImgCropperActivity extends AppCompatActivity {

    Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_cropper);



        readIntent();


        String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();


        UCrop.Options options = new UCrop.Options();

        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(0, 0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000)
                .start(ImgCropperActivity.this);
    }


    private void readIntent(){
        String data = getIntent().getStringExtra("DATA");
        fileUri = Uri.parse(data);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            final  Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri + "");
            setResult(-1, returnIntent);
            finish();
        }
        else if (requestCode == UCrop.RESULT_ERROR){
            final  Throwable cropError = UCrop.getError(data);
        }
    }
}
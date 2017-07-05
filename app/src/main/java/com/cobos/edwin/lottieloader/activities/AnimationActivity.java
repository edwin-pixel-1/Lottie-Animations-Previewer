package com.cobos.edwin.lottieloader.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.cobos.edwin.lottieloader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AnimationActivity extends AppCompatActivity {

    public static String ARG_FILE_PATH;
    private LottieAnimationView lottieAnimationView;
    private String animationJson;
    private String animationAssetsFolder;
    private File animationFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        loadArgs();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(animationFile.getName());
        setupViews();
    }

    private void loadArgs() {
        Intent intent = getIntent();
        animationFile = (File) intent.getSerializableExtra(AnimationActivity.ARG_FILE_PATH);
        animationJson = readFileAsString(animationFile);
        animationAssetsFolder = String.format("%1s/%2s", animationFile.getParent(), "images");
    }

    public String readFileAsString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            FileReader fr = new FileReader(file);
            in = new BufferedReader(fr);
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            Log.e("EDWIN: ", e.toString());
        } catch (IOException e) {
            Log.e("EDWIN: ", e.toString());
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimation();
    }

    private void setupViews() {
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
    }

    private void loadAnimation() {
        if (animationJson != null && !animationJson.isEmpty()) {
            try {
                lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                    @Override
                    public Bitmap fetchBitmap(LottieImageAsset asset) {
                        File imageFile = new File(animationAssetsFolder+"/"+asset.getFileName());

                        if(imageFile.exists()){
                            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            return bmp;
                        }
                        return null;
                    }
                });
                lottieAnimationView.setAnimation(new JSONObject(animationJson));
                lottieAnimationView.loop(false);
                lottieAnimationView.playAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

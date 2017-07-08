package com.cobos.edwin.lottieloader.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
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
    private LinearLayout animationContainer;
    private int[] colorOptions;
    private int currentColorIndex;
    private boolean isAnimationLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        loadArgs();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(animationFile.getName());

        setupViews();

        createColorOptions();

        setBackgroundColor();
    }

    private void loadArgs() {
        Intent intent = getIntent();
        animationFile = (File) intent.getSerializableExtra(AnimationActivity.ARG_FILE_PATH);
        animationJson = readFileAsString(animationFile);
        animationAssetsFolder = String.format("%1s/%2s", animationFile.getParent(), "images");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimation();
    }

    private void setupViews() {
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationContainer = (LinearLayout) findViewById(R.id.animation_container);
    }

    private void createColorOptions() {
        currentColorIndex = 0;
        colorOptions = new int[]{
                R.color.BackgroundOne,
                R.color.BackgroundTwo,
                R.color.BackgroundThree};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.animation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.loop_action:
                switchLoop(item);
                return true;
            case R.id.background_action:
                switchBackgroundColor();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchBackgroundColor() {
        currentColorIndex = currentColorIndex + 1 >= colorOptions.length ? 0 : ++currentColorIndex;
        setBackgroundColor();
    }

    private void setBackgroundColor() {
        animationContainer.setBackgroundColor(ContextCompat.getColor(this, colorOptions[currentColorIndex]));
    }

    private void switchLoop(MenuItem item) {
        isAnimationLoop = !isAnimationLoop;

        cancelAnimation();
        playAnimation();

        String toastMeesage = isAnimationLoop ? "Loop: Enable" : "Loop: Disable";
        Toast.makeText(this, toastMeesage, Toast.LENGTH_LONG).show();
    }

    private void playAnimation() {
        lottieAnimationView.loop(isAnimationLoop);
        lottieAnimationView.playAnimation();
    }

    private void cancelAnimation() {
        if (lottieAnimationView.isAnimating()) {
            lottieAnimationView.cancelAnimation();
        }
    }

    private String readFileAsString(File file) {
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

    private void loadAnimation() {
        if (animationJson != null && !animationJson.isEmpty()) {
            try {
                lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
                    @Override
                    public Bitmap fetchBitmap(LottieImageAsset asset) {
                        File imageFile = new File(animationAssetsFolder + "/" + asset.getFileName());

                        if (imageFile.exists()) {
                            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            return bmp;
                        }
                        return null;
                    }
                });
                lottieAnimationView.setAnimation(new JSONObject(animationJson));
                playAnimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

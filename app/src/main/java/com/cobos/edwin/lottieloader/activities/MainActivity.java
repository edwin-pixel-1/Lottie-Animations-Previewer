package com.cobos.edwin.lottieloader.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cobos.edwin.lottieloader.R;
import com.cobos.edwin.lottieloader.adapters.FilesAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by Edwin Cobos - alejo740@gmail.com
 */
public class MainActivity extends AppCompatActivity implements FilesAdapter.ItemClickListener {

    public static String ARG_DIRECTORY_PATH;
    private static final int REQUEST_STORAGE = 1503;
    private TextView emptyMessage;
    private RecyclerView filesRecyclerView;
    private FilesAdapter filesAdapter;
    private String folder = "Download";
    private String subFolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadArgs();

        setupViews();
        checkPermissions();
    }

    private void loadArgs() {
        Intent intent = getIntent();
        subFolders = intent.hasExtra(MainActivity.ARG_DIRECTORY_PATH) ? intent.getStringExtra(MainActivity.ARG_DIRECTORY_PATH) : "";
        if (!subFolders.isEmpty()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        emptyMessage = (TextView) findViewById(R.id.empty_message);
        filesRecyclerView = (RecyclerView) findViewById(R.id.list_files);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        filesAdapter = new FilesAdapter();
        filesAdapter.setClickListener(this);
        filesRecyclerView.setAdapter(filesAdapter);
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
        searchAnimations();
    }

    private void searchAnimations() {
        File mediaStorageDir = null;

        if (!subFolders.isEmpty()) {
            mediaStorageDir = new File(subFolders);
        } else {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), folder);
        }

        String[] titleP = mediaStorageDir.getPath().split("/" + folder);
        StringBuilder sb = new StringBuilder("/" + folder);
        if (titleP.length > 1) {
            for (int i = 1; i < titleP.length; i++) {
                sb.append(titleP[i]);
            }
            getSupportActionBar().setTitle(sb.toString());
        } else {
            getSupportActionBar().setTitle(mediaStorageDir.getPath());
        }

        if (existDirectory(mediaStorageDir)) {
            File[] files = mediaStorageDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    boolean isJson = s.toLowerCase().endsWith(".json");
                    if (!isJson) {
                        File fileTemp = new File(file.getPath() + "/" + s);
                        if (fileTemp.isDirectory()) {
                            return true;
                        }
                    }
                    return isJson;
                }
            });

            if (files != null && files.length > 0) {
                Log.i("EDWIN", "Created: " + files.length);
                showFilesList(files);
            }
        }
    }

    private void showFilesList(File[] files) {
        filesAdapter.setListData(Arrays.asList(files));
        filesRecyclerView.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);
    }

    private boolean existDirectory(File fileDir) {
        return fileDir.exists() || fileDir.mkdirs();
    }

    private void checkPermissions() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("EDWIN", "Granted permission");
                } else {
                    Log.i("EDWIN", "Denied permission");
                    Toast.makeText(this, "The app was not allowed to write in your storage", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClickItemList(File file) {
        if (!file.isDirectory()) {
            Intent intent = new Intent(this, AnimationActivity.class);
            intent.putExtra(AnimationActivity.ARG_FILE_PATH, file);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.ARG_DIRECTORY_PATH, file.getPath());
            startActivity(intent);
        }
    }
}

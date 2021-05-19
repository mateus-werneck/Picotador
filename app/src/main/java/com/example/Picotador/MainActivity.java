package com.example.Picotador;


import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFmpegExecution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.Picotador.Constant.active;
import static com.example.Picotador.Constant.allMediaList;
import static com.example.Picotador.ListFolder.load_Directory_Files;
import static com.example.Picotador.RecyclerFolderAdapter.allMedia;
import static com.example.Picotador.RecyclerFolderAdapter.count;
import static com.example.Picotador.RecyclerFolderAdapter.holderPositionClicked;
import static com.example.Picotador.RecyclerVideoAdapter.videoCount;
import static com.example.Picotador.StoriesCut.Cut;


public class MainActivity extends AppCompatActivity {


    private RecyclerFolderAdapter recyclerViewAdapter;
    public static RecyclerView recyclerView;
    public static MenuItem delete, share, backButton, itemSelected, config, sobre;
    public static TextView progressText;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Setting Permissions
        String[] permission = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        requestPermissions(permission, 1);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);

        delete = menu.findItem(R.id.delete);
        share = menu.findItem(R.id.share);
        backButton = menu.findItem(R.id.backButton);
        itemSelected = menu.findItem(R.id.itemSelected);
        config = menu.findItem(R.id.config);
        sobre = menu.findItem(R.id.sobre);
        progressText = findViewById(R.id.progressText);
        config.setVisible(false);
        sobre.setVisible(false);


        //RecyclerView
        recyclerView = findViewById(R.id.recyclerviewVideo);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));


        RecyclerVideoAdapter recyclerVideoAdapter = new RecyclerVideoAdapter(this, delete, share, backButton, recyclerView, itemSelected);
        recyclerVideoAdapter.setHasStableIds(true);
        recyclerViewAdapter = new RecyclerFolderAdapter(this, delete, share, backButton, recyclerView, itemSelected);
        recyclerViewAdapter.setHasStableIds(true);

        recyclerView.setAdapter(recyclerViewAdapter);



        backButton.setOnMenuItemClickListener(item -> {
            if (allMediaList.get(0).toString().contains(".mp4")) {
                if (!delete.isVisible()) {
                    allMediaList.clear();
                    allMedia.clear();
                    load_Directory_Files(new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories"));

                    RecyclerFolderAdapter recyclerFolderAdapter = new RecyclerFolderAdapter(getApplicationContext(), menu.findItem(R.id.delete), menu.findItem(R.id.share), menu.findItem(R.id.backButton), recyclerView, menu.findItem(R.id.itemSelected));
                    recyclerView.setAdapter(recyclerFolderAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                    //recyclerFolderAdapter.notifyDataSetChanged();

                    menu.findItem(R.id.backButton).setVisible(false);


                } else {
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.share).setVisible(false);
                    menu.findItem(R.id.itemSelected).setVisible(false);
                    findViewById(R.id.thumb).setLongClickable(true);
                    findViewById(R.id.thumb).setSelected(false);
                    count = 0;
                    videoCount = 0;
                    holderPositionClicked.clear();
                }


            } else {
                menu.findItem(R.id.delete).setVisible(false);
                menu.findItem(R.id.share).setVisible(false);
                menu.findItem(R.id.backButton).setVisible(false);
                menu.findItem(R.id.itemSelected).setVisible(false);
                count = 0;
                videoCount = 0;
                holderPositionClicked.clear();
                findViewById(R.id.thumb).setLongClickable(true);
                findViewById(R.id.thumb).setSelected(false);
            }

            return true;
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (count == 0 && allMediaList.get(0).toString().contains(".mp4")) {
            if (!delete.isVisible()) {
                allMediaList.clear();
                File directory = new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories");
                load_Directory_Files(directory);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                //recyclerViewAdapter.notifyDataSetChanged();
                backButton.setVisible(false);

            } else {
                for (int i = 0; i < videoCount; i++) {
                    CheckBox checkbox = Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.checkBox);
                    Objects.requireNonNull(recyclerView.getLayoutManager().findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.thumb).setLongClickable(true);
                    if (checkbox.isChecked()) {
                        checkbox.setChecked(false);
                        checkbox.setVisibility(INVISIBLE);
                    }
                }
                delete.setVisible(false);
                share.setVisible(false);
                backButton.setVisible(false);
                itemSelected.setVisible(false);
                findViewById(R.id.thumb).setLongClickable(true);
                holderPositionClicked.clear();
                videoCount = 0;
            }

        } else if (videoCount == 0 && !allMediaList.get(0).toString().contains(".mp4")) {
            for (int i = 0; i < count; i++) {
                CheckBox checkbox = Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.checkBox);
                if (checkbox.isChecked()) {
                    checkbox.setChecked(false);
                    checkbox.setVisibility(INVISIBLE);
                }
            }


            delete.setVisible(false);
            share.setVisible(false);
            backButton.setVisible(false);
            itemSelected.setVisible(false);
            holderPositionClicked.clear();
            count = 0;
            findViewById(R.id.thumb).setLongClickable(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                File dataFolder = new File(String.valueOf(getApplicationContext().getDir("cortarStories", MODE_PRIVATE)));
                File[] listFiles = dataFolder.listFiles();

                if (listFiles != null) {
                    for (File listFile : listFiles) {
                        if (listFile.isDirectory()) {
                            File[] listFolder = listFile.listFiles();
                            if (listFolder != null) {
                                for (File file : listFolder) {
                                    file.delete();
                                }
                            } else {
                                listFile.delete();
                            }

                        }
                    }
                    dataFolder.delete();
                }


                File moviesDirectory = new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories");
                if (!moviesDirectory.isDirectory()) {
                    moviesDirectory.mkdirs();
                    findViewById(R.id.selectVideo).setVisibility(VISIBLE);
                } else {
                    if (Objects.requireNonNull(moviesDirectory.listFiles()).length == 0) {
                        findViewById(R.id.selectVideo).setVisibility(VISIBLE);
                    }
                }
                File[] listMoviesFolders = new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories/").listFiles();
                if (listMoviesFolders != null) {
                    for (File listMoviesFolder : listMoviesFolders) {
                        File[] listVideo = listMoviesFolder.listFiles();
                        assert listVideo != null;
                        if (listVideo.length == 0) {
                            listMoviesFolder.delete();
                        }

                    }
                }

                File[] allFolders = new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories").listFiles();
                if (allFolders != null && allFolders.length > 0) {
                    File directory = new File(getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/CortarStories");
                    load_Directory_Files(directory);
                    if (recyclerViewAdapter.getItemCount() > 0) {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                }


            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri fileUri = data.getData();
                getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
            Uri input = data.getData();
            StartCut(input);
            runOnUiThread(() -> {
                findViewById(R.id.selectVideo).setVisibility(INVISIBLE);
                findViewById(R.id.select).setClickable(false);
                findViewById(R.id.progressText).setVisibility(VISIBLE);
                Toast.makeText(getApplicationContext(), "Processando...", Toast.LENGTH_SHORT).show();
            });



        }  else if (requestCode == 105) {
            getSystemService(NotificationManager.class).cancel(104);
        }


    }

    public void selectVideo(View view) {

        if (view.equals(findViewById(R.id.select))) {
            String[] permission = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission("READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission, 2);
            }

            Intent filepicker = new Intent()
                    .setType("video/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(filepicker, "Video"), 3);


        }

    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.config:
                Intent config = new Intent(this, MainActivity.class);
                startActivityForResult(config, 5);
                break;

        }

        return true;


    }
    private void showUserSettings() {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PicotadorSetttings())
                .commit();

    }*/

    public void StartCut (Uri path) {

        DialogCreate Aguarde = new DialogCreate("Aguarde", "Seu video está sendo picotado para caber no Stories.", android.R.drawable.ic_popup_sync);
        Thread StartCut = new Thread(() -> {
            Context context = getApplicationContext();
            String folder = (getApplicationContext().getDir("cortarStories", MODE_PRIVATE)).toString();
            Uri videoFolder = Uri.fromFile(new File(folder + "/stories.mp4"));
            //////////////////////////////////////////// Copying Selected Video //////////////////////////////////////////////////////
            InputStream in = null;
            try {
                in = getApplicationContext().getContentResolver().openInputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            OutputStream out = null;
            try {
                out = getApplicationContext().getContentResolver().openOutputStream(videoFolder);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (in != null && out != null) {
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                assert in != null;
                in.close();
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            NotificationChannel channel = new NotificationChannel("Picotador", "Picotador", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);


            Cut(folder, context, getSupportFragmentManager(), getSystemService(NotificationManager.class), Aguarde, MainActivity.this);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getAdapter().notifyDataSetChanged();
                    if (active) {
                    DialogCreate Pronto = new DialogCreate("Pronto", "Stories picotado!", android.R.drawable.ic_dialog_info);
                    Pronto.show(getSupportFragmentManager(), "video");}
                }
            });




        });
        StartCut.start();
        runOnUiThread(() -> {
            Aguarde.show(getSupportFragmentManager(), "Aguarde");
            findViewById(R.id.selectVideo).setVisibility(INVISIBLE);
            findViewById(R.id.progressBar).setVisibility(VISIBLE);
            ((ProgressBar) findViewById(R.id.progressBar)).setProgress(0);
            findViewById(R.id.cancelButton).setVisibility(VISIBLE);
            findViewById(R.id.cancelButton).setHapticFeedbackEnabled(true);



        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> {
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            StartCut.interrupt();
            SystemClock.sleep(500);
            List<FFmpegExecution> executions = FFmpeg.listExecutions();
            if (executions != null && executions.size() > 0) {
                FFmpeg.cancel();
            }
            findViewById(R.id.progressBar).setVisibility(INVISIBLE);
            findViewById(R.id.cancelButton).setVisibility(INVISIBLE);
            findViewById(R.id.cancelButton).setVisibility(INVISIBLE);
            findViewById(R.id.progressText).setVisibility(INVISIBLE);
            ((TextView)findViewById(R.id.progressText)).setText("0%");
            findViewById(R.id.select).setClickable(true);
            File dataFolder = new File(String.valueOf(getApplicationContext().getDir("cortarStories", MODE_PRIVATE)));
            File[] listFiles = dataFolder.listFiles();

            if (listFiles != null) {
                for (File listFile : listFiles) {
                    if (listFile.isDirectory()) {
                        File[] listFolder = listFile.listFiles();
                        if (listFolder != null) {
                            for (File file : listFolder) {
                                file.delete();
                            }
                        } else {
                            listFile.delete();
                        }

                    }
                }
                dataFolder.delete();
            }
            active = false;
            SystemClock.sleep(100);
            getSystemService(NotificationManager.class).cancel(104);


        });

    }
    @Override
    protected void onStop() {
        super.onStop();
        active=false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        active=false;

    }
    @Override
    protected void onStart() {
        super.onStart();
        active=true;
    }


}





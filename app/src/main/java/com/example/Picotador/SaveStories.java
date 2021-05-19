package com.example.Picotador;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SaveStories {

public static void SaveStories(ArrayList<File> allStories, File Stories, File dataFolder, Context context) {
    File finalFolder = null;
    OutputStream galleryFolder = null;
    InputStream appFolder = null;
    String storiesName;
    for (int i = 0; i < 99; i++) {
        if (i <= 8) {
            if (!new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "CortarStories" + File.separator + "Stories0" + (i + 1)).exists()) {
                finalFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/CortarStories/Stories0" + (i + 1));
                if (finalFolder.mkdirs()) {
                    break;
                }
            }
        } else {
            if (!new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "CortarStories" + File.separator + "Stories" + (i + 1)).exists()) {
                finalFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/CortarStories/Stories" + (i + 1));
                if (finalFolder.mkdirs()) {
                    break;
                }
            }
        }
    }

    if (allStories.size() > 0) {
        for (int i = 0; i < allStories.size(); i++) {
            if (i <= 8) { storiesName = "/stories0" + (i + 1) + ".mp4";
            } else { storiesName = "/stories" + (i+1) + ".mp4";}
            File story = allStories.get(i);
            try {
                appFolder = context.getContentResolver().openInputStream(Uri.fromFile(story));
                galleryFolder = context.getContentResolver().openOutputStream(Uri.fromFile(new File(finalFolder + storiesName)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                byte[] buf = new byte[1024];
                int len;
                if (appFolder != null) {
                    while ((len = appFolder.read(buf)) > 0) {
                        if (galleryFolder != null) {
                            galleryFolder.write(buf, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    try {
        assert appFolder != null;
        appFolder.close();
        assert galleryFolder != null;
        galleryFolder.close();
        File [] storiesList = Stories.listFiles();
        assert storiesList != null;
        for (File file : storiesList) {
            file.delete();
        }
        dataFolder.delete();
        Stories.delete();
    } catch (IOException e) {
        e.printStackTrace();
    }

}

}

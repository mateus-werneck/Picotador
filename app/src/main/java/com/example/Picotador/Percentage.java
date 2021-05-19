package com.example.Picotador;

import android.os.AsyncTask;

public class Percentage extends AsyncTask<StoriesCut, String, String> {
    protected String doInBackground(StoriesCut... percentage) {
        String finalPercentage = StoriesCut.percentage;
        publishProgress(finalPercentage);
        return finalPercentage;
    }

    protected void onProgressUpdate(String... progress) {
        MainActivity.progressText.setText(progress[0]);
    }

}


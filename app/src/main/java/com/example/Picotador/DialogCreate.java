package com.example.Picotador;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import static com.example.Picotador.Constant.allMediaList;
import static com.example.Picotador.ListFolder.load_Directory_Files;
import static com.example.Picotador.MainActivity.backButton;
import static com.example.Picotador.MainActivity.delete;
import static com.example.Picotador.MainActivity.itemSelected;
import static com.example.Picotador.MainActivity.recyclerView;
import static com.example.Picotador.MainActivity.share;

public class DialogCreate extends DialogFragment {

    private final String title;
    private final String message;
    private final int icon;


    public DialogCreate(String title, String message, int icon) {
        this.title = title;
        this.message = message;
        this.icon = icon;
    }


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity());
        Dialog.setTitle(title);
        Dialog.setMessage(message);
        Dialog.setIcon(icon);
        Dialog.setPositiveButton("OK", null);

    return Dialog.show();
    }
}





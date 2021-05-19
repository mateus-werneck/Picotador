package com.example.Picotador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.OnLongClickListener;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.startActivity;
import static com.example.Picotador.Constant.allMediaList;
import static java.util.Objects.requireNonNull;


public class RecyclerFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final MenuItem delete;
    private final MenuItem share;
    private final MenuItem backButton;
    private final MenuItem itemSelected;
    private final RecyclerView recyclerView;



    RecyclerFolderAdapter(Context context, @Nullable MenuItem delete, @Nullable MenuItem share, @Nullable MenuItem backButton, RecyclerView recyclerView, @Nullable MenuItem itemSelected){
        this.context = context;
        this.delete = delete;
        this.share = share;
        this.backButton = backButton;
        this.recyclerView = recyclerView;
        this.itemSelected = itemSelected;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery,parent,false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        File[] filesList = allMediaList.get(position).listFiles();


        assert filesList != null;
        allMedia.clear();
        allMedia.add(filesList[0]);

        String seconds = String.valueOf(System.currentTimeMillis());
        Uri uri = Uri.fromFile(allMedia.get(0));


        Glide.with(context)
                .load(uri)
                .thumbnail(0.1f)
                   //.apply(new RequestOptions().signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(((FileLayoutHolder)holder).thumb);

        ((FileLayoutHolder)holder).videoTitle.setText(allMediaList.get(position).getName() + " (" + requireNonNull(allMediaList.get(position).listFiles()).length + ")");
        ((FileLayoutHolder) holder).checkBox.setVisibility(INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return allMediaList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class FileLayoutHolder extends RecyclerView.ViewHolder {

        final ImageView thumb;
        final TextView videoTitle;
        final CheckBox checkBox;


        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            videoTitle =  (TextView) itemView.findViewById(R.id.videoTitle);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);




        thumb.setOnClickListener(v -> {

            thumb.setHapticFeedbackEnabled(true);
            if (holderPositionClicked.size() > 0) {
                String adapterPosition = String.valueOf(getAbsoluteAdapterPosition());
                for (int i = 0; i < holderPositionClicked.size(); i++) {
                    if (holderPositionClicked.get(i).equals(adapterPosition)) {
                        holderPositionClicked.remove(i);

                        checkBox.setChecked(false);
                        checkBox.setVisibility(INVISIBLE);
                        String text = ((count - 1 + "/" + allMediaList.size()));
                        itemSelected.setTitle(text);



                        count -= 1;
                        if (count == 0) {
                            itemSelected.setVisible(false);
                            delete.setVisible(false);
                            backButton.setVisible(false);
                            share.setVisible(false);
                            thumb.setLongClickable(true);

                        } else if (count == 1) {
                            share.setVisible(true);
                        }
                        return;
                        }
                }

                holderPositionClicked.add(adapterPosition);
                count += 1;

                checkBox.setChecked(true);
                checkBox.setVisibility(VISIBLE);
                String text = ((count + "/" + allMediaList.size()));
                itemSelected.setTitle(text);
                itemSelected.setVisible(true);

                delete.setVisible(true);
                backButton.setVisible(false);
                share.setVisible(false);


            } else {
                holderPositionClicked.size();
                File displayFolder = new File (allMediaList.get(getAbsoluteAdapterPosition()).toString());
            ListVideos.load_Directory_Files(displayFolder);
            RecyclerVideoAdapter adapter = new RecyclerVideoAdapter(context, delete, share, backButton, recyclerView, itemSelected);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            adapter.notifyDataSetChanged();
            backButton.setVisible(true);
            }


        });

        thumb.setOnLongClickListener(v -> {
        thumb.setSelected(true);
        holderPositionClicked.add(String.valueOf(getAbsoluteAdapterPosition()));
        count += 1;

        checkBox.setChecked(true);
        checkBox.setVisibility(VISIBLE);
        String text = ((count + "/" + allMediaList.size()));
        itemSelected.setTitle(text);
        itemSelected.setVisible(true);

        delete.setVisible(true);
        share.setVisible(true);
        backButton.setVisible(false);
        thumb.setLongClickable(false);



            delete.setOnMenuItemClickListener(item -> {
                if (holderPositionClicked.size() > 1) {
                    for (int i = 0; i < holderPositionClicked.size(); i++) {
                        File deleteFolder = new File(String.valueOf(allMediaList.get(Integer.parseInt(holderPositionClicked.get(i)))));
                        if (deleteFolder.isDirectory()) {
                            File[] listFolder = deleteFolder.listFiles();
                            assert listFolder != null;
                            for (File file : listFolder) {
                                file.delete();
                            }

                        }
                        deleteFolder.delete();
                    }
                } else if (holderPositionClicked.size() == 1) {
                    File deleteFolder = new File(allMediaList.get(Integer.parseInt(holderPositionClicked.get(0))).toString());

                    if (deleteFolder.isDirectory()) {
                        File[] listFolder = deleteFolder.listFiles();
                        assert listFolder != null;
                        for (File file : listFolder) {
                            file.delete();
                        }
                    }
                    deleteFolder.delete();

                }

                new Thread(() -> Glide.get(context).clearDiskCache()).start();

                Glide.with(itemView).clear(thumb);
                Glide.get(context).clearMemory();
                allMedia.clear();
                allMediaList.clear();


                count = 0;
                holderPositionClicked.clear();
                ListFolder.load_Directory_Files(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/CortarStories"));

                RecyclerFolderAdapter recyclerFolderAdapter = new RecyclerFolderAdapter(context, delete, share, backButton, recyclerView, itemSelected);

                recyclerView.setAdapter(recyclerFolderAdapter);

                recyclerFolderAdapter.notifyDataSetChanged();

                delete.setVisible(false);
                share.setVisible(false);
                itemSelected.setVisible(false);
                backButton.setVisible(false);


                return true;
            });
            share.setOnMenuItemClickListener(item -> {
                File shareFolder = new File(String.valueOf(allMediaList.get(Integer.parseInt(holderPositionClicked.get(0)))));
                File[] listFile = shareFolder.listFiles();
                ArrayList<Uri> uris = new ArrayList<>();

                assert listFile != null;
                for (File file : listFile) {
                    uris.add(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file));

                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("video/mp4");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Compartilhar em");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Compartilhando...");


                startActivity(context, Intent.createChooser(sharingIntent, "Compartilhar Vídeo").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null);


                delete.setVisible(false);
                share.setVisible(false);
                itemSelected.setVisible(false);
                backButton.setVisible(false);
                thumb.setLongClickable(true);
                checkBox.setChecked(false);
                checkBox.setVisibility(INVISIBLE);
                holderPositionClicked.clear();
                count = 0;


                return true;
            });


            return true;
        });



        }


    }
    public static ArrayList<String> holderPositionClicked = new ArrayList<>();
    public static ArrayList<File> allMedia = new ArrayList<>();
    public static int count = 0;

}

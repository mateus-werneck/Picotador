package com.example.Picotador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.startActivity;
import static com.example.Picotador.Constant.allMediaList;
import static com.example.Picotador.RecyclerFolderAdapter.allMedia;
import static com.example.Picotador.RecyclerFolderAdapter.holderPositionClicked;


public class RecyclerVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private MenuItem delete, share, backButton, itemSelected;
    private RecyclerView recyclerView;

    RecyclerVideoAdapter(Context context, MenuItem delete, MenuItem share, MenuItem backButton, RecyclerView recyclerView, MenuItem itemSelected){
        this.context = context;
        this.delete = delete;
        this.share = share;
        this.backButton = backButton;
        this.recyclerView = recyclerView;
        this.itemSelected = itemSelected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_video,parent,false);


        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Uri uri = Uri.fromFile(Constant.allMediaList.get(position));

        Glide.with(context)
                .load(uri)
                .thumbnail(0.1f)
                //.apply(new RequestOptions().signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(((FileLayoutHolder)holder).thumb);
        ((FileLayoutHolder)holder).videoTitle.setText(allMediaList.get(position).getName());




    }

    @Override
    public int getItemCount() {
        return Constant.allMediaList.size();
    }


    class FileLayoutHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView videoTitle;
        CheckBox checkBox;


        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            videoTitle =  (TextView) itemView.findViewById(R.id.videoTitle);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);



            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holderPositionClicked.size() > 0) {
                        String adapterPosition = String.valueOf(getBindingAdapterPosition());
                        for (int i = 0; i < holderPositionClicked.size(); i++) {
                            if (holderPositionClicked.get(i).equals(adapterPosition)) {
                                holderPositionClicked.remove(i);
                                checkBox.setChecked(false);
                                checkBox.setVisibility(INVISIBLE);
                                String text = ((videoCount - 1 + "/" + allMediaList.size()));
                                itemSelected.setTitle(text);
                                videoCount -= 1;
                                if (holderPositionClicked.size() == 0) {
                                    itemSelected.setVisible(false);
                                    delete.setVisible(false);
                                    share.setVisible(false);}
                                return;
                            }
                        }

                        holderPositionClicked.add(adapterPosition);
                        videoCount += 1;

                        checkBox.setChecked(true);
                        checkBox.setVisibility(VISIBLE);
                        String text = ((videoCount + "/" + allMediaList.size()));
                        itemSelected.setTitle(text);
                        itemSelected.setVisible(true);

                        delete.setVisible(true);
                        backButton.setVisible(true);
                        share.setVisible(true);

                    } else {
                    File video = new File (allMediaList.get(getAbsoluteAdapterPosition()).toString());
                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", video);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri,"video/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(context, Intent.createChooser(intent, "Visualizar Vídeo").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null);}


                }
            });

            thumb.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    thumb.setSelected(true);
                    holderPositionClicked.add(String.valueOf(getBindingAdapterPosition()));
                    videoCount += 1;

                    checkBox.setChecked(true);
                    checkBox.setVisibility(VISIBLE);
                    String text = ((videoCount + "/" + allMediaList.size()));
                    itemSelected.setTitle(text);
                    itemSelected.setVisible(true);


                    delete.setVisible(true);
                    share.setVisible(true);
                    backButton.setVisible(true);
                    thumb.setLongClickable(false);

                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (holderPositionClicked.size() > 0) {
                                File parentFolder= null;

                                for (int i = 0; i < holderPositionClicked.size(); i++) {
                                    File deleteFolder = new File(String.valueOf(allMediaList.get(Integer.parseInt(holderPositionClicked.get(i)))));
                                    parentFolder = deleteFolder.getParentFile();
                                    deleteFolder.delete();
                                    }


                                assert parentFolder != null;
                                if (parentFolder.listFiles() != null && Objects.requireNonNull(parentFolder.listFiles()).length == 0) {
                                    parentFolder.delete();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.get(context).clearDiskCache();

                                        }
                                    }).start();

                                    Glide.with(itemView).clear(thumb);
                                    Glide.get(context).clearMemory();
                                    allMedia.clear();
                                    allMediaList.clear();
                                    videoCount = 0;
                                    holderPositionClicked.clear();
                                    ListFolder.load_Directory_Files(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/CortarStories"));

                                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

                                    RecyclerFolderAdapter recyclerFolderAdapter = new RecyclerFolderAdapter(context, delete, share, backButton, recyclerView, itemSelected);

                                    recyclerView.setAdapter(recyclerFolderAdapter);

                                    recyclerFolderAdapter.notifyDataSetChanged();

                                    delete.setVisible(false);
                                    share.setVisible(false);
                                    checkBox.setChecked(false);
                                    backButton.setVisible(false);
                                    itemSelected.setVisible(false);

                                    for (int i = 0; i < videoCount; i++) {
                                        CheckBox checkbox = Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.checkBox);
                                        checkbox.setChecked(false);
                                        checkbox.setVisibility(View.INVISIBLE);
                                    }

                                    holderPositionClicked.clear();
                                    videoCount = 0;

                                    return true;
                                } else if (parentFolder.listFiles() != null && Objects.requireNonNull(parentFolder.listFiles()).length > 0) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.get(context).clearDiskCache();

                                        }
                                    }).start();

                                    Glide.with(itemView).clear(thumb);
                                    Glide.get(context).clearMemory();
                                allMedia.clear();
                                allMediaList.clear();
                                videoCount = 0;
                                holderPositionClicked.clear();
                                ListVideos.load_Directory_Files(parentFolder);


                                RecyclerVideoAdapter recyclerVideoAdapter = new RecyclerVideoAdapter(context, delete, share, backButton, recyclerView, itemSelected);

                                recyclerView.setAdapter(recyclerVideoAdapter);

                                recyclerVideoAdapter.notifyDataSetChanged();

                                delete.setVisible(false);
                                share.setVisible(false);
                                itemSelected.setVisible(false);
                                backButton.setVisible(true);

                                    for (int i = 0; i < videoCount; i++) {
                                        CheckBox checkbox = Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.checkBox);
                                        checkbox.setChecked(false);
                                        checkbox.setVisibility(View.INVISIBLE);
                                    }

                                    holderPositionClicked.clear();
                                    videoCount = 0;

                                return true;}

                            }
                        return true;
                        }
                    });
                share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            ArrayList<File> shareFile = new ArrayList<>();
                            ArrayList<Uri> uris = new ArrayList<>();

                            for (int i =0; i < holderPositionClicked.size(); i++) {
                            shareFile.add(new File (String.valueOf(allMediaList.get(Integer.parseInt(holderPositionClicked.get(i))))));
                            uris.add(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", shareFile.get(i)));
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
                                backButton.setVisible(true);
                                thumb.setLongClickable(true);

                                for (int i = 0; i < videoCount; i++) {
                                    CheckBox checkbox = Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(Integer.parseInt(holderPositionClicked.get(i)))).findViewById(R.id.checkBox);
                                    checkbox.setChecked(false);
                                    checkbox.setVisibility(View.INVISIBLE);
                                }


                                holderPositionClicked.clear();
                                videoCount = 0;
                                return true;
                        }
                    });


                    return true;
                }
            });



        }

    }
    public static int videoCount = 0;
}

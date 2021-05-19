
package com.example.Picotador;

import java.io.File;
import java.util.Collections;

import static com.example.Picotador.Constant.allMediaList;

public class ListVideos {

    public static void load_Directory_Files(File directory){
        allMediaList.clear();
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (File file : fileList) {
                if (file.isDirectory()) {
                    load_Directory_Files(file);
                } else {
                    allMediaList.add(file);


                }
            }
        }
    Collections.sort(allMediaList);
    }


}

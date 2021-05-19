
package com.example.Picotador;

import java.io.File;
import java.util.Collections;

import static com.example.Picotador.Constant.allMediaList;

public class ListFolder {

    public static void load_Directory_Files(File directory){

        if (allMediaList.size() > 0) {
            allMediaList.clear();
        }
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (File file : fileList) {
                if (file.isDirectory()) {
                    Constant.allMediaList.add(file);
                } else {

                    break;

                }
            }
        }

    Collections.sort(allMediaList);
    }

}



package com.example.androide.Utilities;

import android.util.Log;

import java.io.File;

public class FilesUtil {

    public boolean createFile(String fileName, String destinationPath) {
        try {
            File file = new File(destinationPath + "/" + fileName);
            return file.createNewFile();
        } catch (Exception e) {
            Log.d("File Creation", "Error Occoured While File Creation");
            e.printStackTrace();
        }
        return false;
    }

}

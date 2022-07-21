package com.example.androide.Utilities;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AssetsUtil {

    Context context;

    public AssetsUtil(Context context) {
        this.context = context;
    }

    public void copyAssets(String assetName, String destinationPath) {
        BufferedInputStream bis = null;
        OutputStream os = null;
        int BUF_SIZE = 8 * 1024;
        try {
            bis = new BufferedInputStream(context.getAssets().open(assetName));
            os = new BufferedOutputStream(
                    new FileOutputStream(destinationPath+"/"+assetName));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                os.write(buf, 0, len);
            }
            os.close();
            bis.close();
        } catch (Exception e) {
            System.err.println("Error while copying assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

package com.youdao.sdk.ocrdemo;

import java.io.FileInputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.ImageView;

public class ImageUtils {
    public static Bitmap readBitmapFromFile(String filePath, int size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > size || srcWidth > size) {
            if (srcWidth < srcHeight) {
                inSampleSize = Math.round(srcHeight / size);
            } else {
                inSampleSize = Math.round(srcWidth / size);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

}

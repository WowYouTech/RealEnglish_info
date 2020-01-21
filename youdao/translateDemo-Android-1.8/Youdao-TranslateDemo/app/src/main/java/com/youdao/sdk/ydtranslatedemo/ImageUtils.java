package com.youdao.sdk.ydtranslatedemo;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.youdao.sdk.app.EncryptHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {
    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            int srcWidth = bitmap.getWidth();
            int srcHeight = bitmap.getHeight();
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap compressBitmap(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            bitmap = decodeBitmap(context, uri, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        int size = 768;
        if (srcHeight > size || srcWidth > size) {
            if (srcWidth < srcHeight) {
                inSampleSize = Math.round(srcHeight / size);
            } else {
                inSampleSize = Math.round(srcWidth / size);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        try {
            bitmap = decodeBitmap(context, uri, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int angle = readPictureDegree(uri.getPath());
        return rotaingImageView(angle,bitmap);
    }


    public static Bitmap decodeBitmap(Context context, Uri uri,
                                      BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (uri != null) {
            InputStream inputStream = null;
            try {
                /**
                 * 将图片的Uri地址转换成一个输入流
                 */
                if (uri.toString().startsWith("content://")) {
                    ContentResolver cr = context.getContentResolver();
                    inputStream = cr.openInputStream(uri);
                } else {
                    inputStream = new FileInputStream(uri.toString());
                }

                /**
                 * 将输入流转换成Bitmap
                 */
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                if(inputStream != null){
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] datas = baos.toByteArray();
        String base64 = EncryptHelper.getBase64(datas);
        return base64;
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin 原图
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int degree) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 生成文件路径和文件名
     *
     * @return
     */
    public static String getFileName() {
        String saveDir = Environment.getExternalStorageDirectory() + "/myPic";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir(); // 创建文件夹
        }
        //用日期作为文件名，确保唯一性
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fileName = saveDir + "/" + formatter.format(date) + ".png";

        return fileName;
    }

//    //某个anchor被点击
//    public void onAnchorClick(int id, Activity context) {
//        try {
//            String text = "";
//            if (!TextUtils.isEmpty(text)) {
//                Translate translate = EnWordTranslator.lookupNative(text);
//                if (translate != null && translate.success()) {
//                    TranslateData td = new TranslateData(System.currentTimeMillis(),
//                            translate);
//                    TranslateDetailActivity.open(context, td);
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
}

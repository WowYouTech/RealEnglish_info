package com.yy.libcommon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by steveyang on 26/11/16.
 */

public class FileManager {
    
    private static FileManager ourInstance;



    public static FileManager instance() {
        if(ourInstance == null){
            ourInstance = new FileManager();
        }
        return ourInstance;
    }

    public static Bitmap readBitmapFromPath(String path){

        Bitmap mImageBitmap = null;
        if(path != null){
            File file = new File(path);
            if(file.exists()){
                mImageBitmap = BitmapFactory.decodeFile(path);
                if(mImageBitmap == null) {
                    file.delete();
                }
            }
        }

        return mImageBitmap;
    }

    public static File createFilePath(String fileFullPath) {

        File file = new File(fileFullPath);
        String dir = file.getParent();
        if(null == dir){
            return file;
        }
        if(file.exists()){
            return file;
        }

        //Make file directory
        File fileDir = new File(dir);
        if(!fileDir.exists()){
            try {
                if (fileDir.mkdirs()) {
                    System.out.println("Collateral directory "+ dir +" created");
                } else {
                    System.out.println("Collateral directory  " + dir + " is not created");
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getFileDir() {

        String storageDir = FileManager.getLocalFileDir();

        return storageDir;
    }

    public static File createLocalImageFile(String fileName) {

        String imageFileName;
        if (fileName != null){
            imageFileName = fileName;
        }else{
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timeStamp + "_";
        }

        String s = getLocalFileDir() + File.separator + imageFileName + ".jpg";
        File image = createFilePath(s);

        return image;
    }

    public static void copyFile(File sourceFile, File destFile)  {

        if (!destFile.exists()) {
            createFilePath(destFile.getAbsolutePath());
        }

        FileChannel source = null;
        FileChannel destination;
        destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        catch (Exception e){

        }
        finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void safeDeleteFile(Context context, final String path){

        if(null == path){
            return;
        }

        File to = new File(path);
        if(to.exists()) {
            to.delete();
        }else{
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else{

            String[] files = {path};
            MediaScannerConnection.scanFile(context, files, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });

        }
    }

    public static String getFileTypeString(){
        return ".jpg";
    }

    public static String getLocalFileFolder(){
        return LibConfig.IMAGE_DIRECTORY;
    }

    public static String existingLocalFilePath(int key, int fileType){
        File file = FileManager.getLocalFileInstance(key);
        if (file.exists()) {
            return file.getPath();
        }
        return null;
    }

    public static String getLocalFilePath(int key, int fileType){

        String s = Environment.getExternalStorageDirectory()
                + File.separator + LibConfig.BASE_DIRECTOY
                + File.separator + FileManager.getLocalFileFolder()
                + File.separator + key + FileManager.getFileTypeString();

        return s;
    }

    public static File getLocalFileInstance(int key){

        File newFile = new File(Environment.getExternalStorageDirectory()
                + File.separator + LibConfig.BASE_DIRECTOY
                + File.separator + FileManager.getLocalFileFolder()
                + File.separator + key + FileManager.getFileTypeString());

        return newFile;
    }

    public static String getLocalFileDir(){

        String s = Environment.getExternalStorageDirectory()
                + File.separator + LibConfig.BASE_DIRECTOY
                + File.separator + FileManager.getLocalFileFolder();

        return s;
    }

    public static boolean isFileExist(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

}

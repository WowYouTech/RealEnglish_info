package com.yy.libcommon;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

/**
 * ====================================
 * Created by michael.carr on 19/02/14.
 * ====================================
 */
public class Util {

    public final static int MAX_UPLOAD_IMAGE_SIZE = 480;

    public static ArrayList<String> arrayListFromStrings(String[] ar){

        ArrayList<String> list = new ArrayList<>();
        for(String s : ar ){
            list.add(s);
        }
        return list;
    }


    public static String generateDeviceId(Context pContext) {

        final String macAddr, androidId;

        WifiManager wifiMan = (WifiManager) pContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        macAddr = wifiInf.getMacAddress();
        androidId = "" + android.provider.Settings.Secure.getString(pContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid;

        try {

            deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());

        } catch (NullPointerException e){//Running emulator

            e.printStackTrace();

            Random r = new Random();
            int rand = r.nextInt(100000-1000) + 1000;

            return String.valueOf(rand);
        }

        return deviceUuid.toString();

    }

    public static String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }

    public static int dpToPx(int dp, Context pContext) {
        //DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
        //int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        if(null == pContext){
            return 0;
        }
        Resources r = pContext.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());

        return Math.round(px);
    }

    public static int dpToPx(float dp, Context pContext) {
        //DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
        //int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        Resources r = pContext.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());

        return Math.round(px);
    }

    /**
     * Returns the correct suffix for the last digit (1st, 2nd, .. , 13th, .. , 23rd)
     */
    public static String getLastDigitSufix(int number) {
        switch( (number<20) ? number : number%10 ) {
            case 1 : return "st";
            case 2 : return "nd";
            case 3 : return "rd";
            default : return "th";
        }
    }


    public static String getRandomInstanceId() {

        Random random = new Random();
        int r = random.nextInt();
        r = abs(r) % 1000000;
        return r + "";
    }

    public static String getRandomString() {

        String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int RANDOM_STR_LENGTH = 12;

        StringBuffer randStr = new StringBuffer();

        for (int i = 0; i < RANDOM_STR_LENGTH; i++) {

            int number = getRandomNumber();
            char ch = _CHAR.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    public static int getRandomNumber(){

        String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        Random random = new Random();

        int randomInt = 0;

        randomInt = random.nextInt(_CHAR.length());

        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }

    }

    public static boolean isStringEmpty(String input){
        return (input != null && input.length() == 0);
    }

    public static void setLayoutClickable(View view, boolean enabled) {

        view.setFocusable(enabled);
        view.setClickable(enabled);

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                if (((ViewGroup) view).getChildAt(i) instanceof ListView) {
                    //Do nothing
                } else {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setLayoutClickable(innerView, enabled);
                }
            }
        }
    }

    public static Bitmap decodeFile(String path, int maxSize){
        File file = new File(path);
        return decodeFile(file,maxSize);
    }
    public static Bitmap decodeFile(File f, int maxSize){

        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;

            if (o.outHeight > maxSize || o.outWidth > maxSize) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(maxSize /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPurgeable = true;

            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return b;

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }

    public static byte[] preProcessBitmapToByteArray(String pathToOurFile) {

        Bitmap bitmap = BitmapFactory.decodeFile(pathToOurFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final int maxSize = MAX_UPLOAD_IMAGE_SIZE;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap preProcessBitmap(Bitmap bitmap,ByteArrayOutputStream byteArrayOutputStream) {


        final int maxSize = MAX_UPLOAD_IMAGE_SIZE;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);

        if(byteArrayOutputStream != null){
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        }

        return resizedBitmap;
    }

    public static Bitmap decodeFileFixOrientation(String file) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap tb = BitmapFactory.decodeFile(file, opts);
        Bitmap bm = preProcessBitmap(tb,null);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return rotatedBitmap;
    }


    public static String bigDecimalToPriceStr(BigDecimal bigDecimal){
        return "$" + bigDecimal.setScale(2, RoundingMode.CEILING).toString();
    }

    public static boolean bigDecimalsAreTheSame(BigDecimal bd1, BigDecimal bd2){

        if (bd1 == null && bd2 == null){
            return true;
        }

        if (bd2 != null) {
            if (bd1 == null && bd2.compareTo(BigDecimal.ZERO) == 0) {
                return true;
            }
        }

        if (bd1 != null) {
            if (bd1.compareTo(BigDecimal.ZERO) == 0 && bd2 == null) {
                return true;
            }
        }

        if (bd1 != null && bd2 != null){
            if (bd1.compareTo(bd2) == 0){
                return true;
            }
        }

        return false;
    }

    public static boolean integersAreTheSame(Integer i1, Integer i2){

        if (i1 == null && i2 == null){
            return true;
        }

        if (i2 != null) {
            if (i1 == null && i2.equals(0)) {
                return true;
            }
        }

        if (i1 != null) {
            if (i1.equals(0) && i2 == null) {
                return true;
            }
        }

        if (i1 != null && i2 != null){
            if (i1.equals(i2)){
                return true;
            }
        }

        return false;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {

        Cursor cursor = null;

        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
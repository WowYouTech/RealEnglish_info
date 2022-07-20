package com.yy.libcommon.Font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by steve.yang on 16/11/16.
 */

public class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }

    public static Typeface getTypeFace(Context context, String ttf) {

        //GoogleSans_
        if(ttf == null || ttf.length()==0){
            Typeface customFont = FontCache.getTypeface("GoogleSans_Regular.ttf", context);
            return  customFont;
        }else{

            if(ttf.equals("bold")){
                Typeface customFont = FontCache.getTypeface("GoogleSans_Bold.ttf", context);
                return  customFont;
            }
            else if (ttf.equals("medium")){
                Typeface customFont = FontCache.getTypeface("GoogleSans_Medium.ttf", context);
                return  customFont;
            }
            else {
                Typeface customFont = FontCache.getTypeface("GoogleSans_Regular.ttf", context);
                return  customFont;
            }
        }

        //Aller
//        if(ttf == null || ttf.length()==0){
//
//            Typeface customFont = FontCache.getTypeface("Aller_Std_Rg.ttf", context);
//            return customFont;
//        }else{
//
//            if(ttf.equals("bold")){
//                Typeface customFont = FontCache.getTypeface("Aller_Std_Bd.ttf", context);
//                return customFont;
//            }else if (ttf.equals("light")){
//                Typeface customFont = FontCache.getTypeface("Aller_Std_Lt.ttf", context);
//                return customFont;
//            }
//            else {
//                Typeface customFont = FontCache.getTypeface("Aller_Std_Rg.ttf", context);
//                return customFont;
//            }
//        }

        //Roboto
//        if(ttf == null || ttf.length()==0){
//
//            Typeface customFont = FontCache.getTypeface("Roboto-Regular.ttf", context);
//            return  customFont;
//        }else{
//
//            if(ttf.equals("bold")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Bold.ttf", context);
//                return  customFont;
//            }else if (ttf.equals("light")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Light.ttf", context);
//                return  customFont;
//            }
//            else if (ttf.equals("medium")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Medium.ttf", context);
//                return  customFont;
//            }
//            else if (ttf.equals("thin")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Thin.ttf", context);
//                return  customFont;
//            }
//            else {
//                Typeface customFont = FontCache.getTypeface("Roboto-Regular.ttf", context);
//                return  customFont;
//            }
//        }

        //OpenSans
//        if(ttf == null || ttf.length()==0){
//
//            Typeface customFont = FontCache.getTypeface("Roboto-Regular.ttf", context);
//            return  customFont;
//        }else{
//
//            if(ttf.equals("bold")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Bold.ttf", context);
//                return  customFont;
//            }
//            else if (ttf.equals("light")){
//                Typeface customFont = FontCache.getTypeface("Roboto-Light.ttf", context);
//                return  customFont;
//            }
//            else if (ttf.equals("medium")){
//                Typeface customFont = FontCache.getTypeface("Roboto-SemiBold.ttf", context);
//                return  customFont;
//            }
//            else {
//                Typeface customFont = FontCache.getTypeface("Roboto-Regular.ttf", context);
//                return  customFont;
//            }
//        }

//        OpenSans-Bold.ttf
//        OpenSans-BoldItalic.ttf
//        OpenSans-ExtraBold.ttf
//        OpenSans-ExtraBoldItalic.ttf
//        OpenSans-Light.ttf
//        OpenSans-LightItalic.ttf
//        OpenSans-Regular.ttf
//        OpenSans-RegularItalic.ttf
//        OpenSans-SemiBold.ttf
//        OpenSans-SemiBoldItalic.ttf

    }
}

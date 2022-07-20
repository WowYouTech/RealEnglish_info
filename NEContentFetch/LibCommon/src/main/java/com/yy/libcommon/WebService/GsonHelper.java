package com.yy.libcommon.WebService;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by steve.yang on 8/09/16.
 */
public class GsonHelper {

    public static <T> T objectFromString(String data, Class<T> dataClass){

        Gson gson = new Gson();

        T object = gson.fromJson(data,dataClass);
        return object;
    }

    public static JSONObject fromObjectNoNamePolicy(Object object) {
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().create();


        String s = gson.toJson(object);

        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject(s);

        } catch (JSONException e) {

            e.printStackTrace();

            return null;
        }

        return jsonObject;

    }

    public static JSONObject fromObject(Object object) {
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();


        String s = gson.toJson(object);

        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject(s);

        } catch (JSONException e) {

            e.printStackTrace();

            return null;
        }

        return jsonObject;

    }

    public static String stringFromObjectNoPolicy(Object object){
        JSONObject jsonObject = fromObjectNoNamePolicy(object);

        String s = "";

        try {
            s = jsonObject.toString();
        }catch (Exception e){

        }

        return s;
    }
}

package com.nativeenglish.tool;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nativeenglish.tool.models.ImitateItem;
import com.nativeenglish.tool.models.ImitateSession;
import com.nativeenglish.tool.models.LocalTranslatedItem;
import com.nativeenglish.tool.models.SrtItem;
import com.nativeenglish.tool.models.WordItem;
import com.yy.libcommon.CommonLib.GsonHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContentParser {

    static String ww1 =
            "cesspool=[ˈsespuːl] =\u200Ba large underground hole or container that is used for collecting and storing solid waste, urine, and dirty water=污水坑；粪坑；污秽场所=??It's a cesspool of COVID-19 being passed around." +
                    "\n" +
                    "jerk=[dʒɜːrk]=a stupid person, usually a man.=<美，非正式>傻瓜，坏蛋；=??This guy's a jerk." +
                    "\n" +
                    "slammed=[slæmd]=to put, push or throw something into a particular place or position with a lot of force=v. 猛烈抨击（slam 的过去分词）；猛撞=??So, I grabbed a couple of Snickers bars and things and slammed in the back of her shoe, under her heel." +
                    "\n" +
                    "propped=[prɔpt]=to support something physically, often by leaning it against something else or putting something under it=支撑，支持，维持( prop的过去式和过去分词 )=??So I propped her up, that is quite short, walked up, was like, what about now?" +
                    "\n" +
                    "strapped=[stræpt]=a narrow piece of leather or other strong material used for fastening something or giving support=用带子系（strap 的过去式和过去分词）=??We're at the top, and I'm looking at her, she's strapped in and the seat is massive on her." +
                    "\n" +
                    "chill=[tʃɪl]=being cool=<非正式>放松；=??I've been quite chill about dating lately."
                    +"\n"
                    + "#=[]=1. A hashtag- this is generally in an online context" +
                    "\n2. A hash or pound- this is a symbol on the phone" +
                    "\n3. Number- the “#” symbol is useful as an abbreviated form of the word number, " +
                    "and is generally placed before a numeral."
                    + "=井号键；" +"--"

            ;

//            "stilts\n" +
//            "sorted through\n" +
//            "debit card\n" +
//            "vegan\n" +
//            "reflect upon\n" +
//            "module=['mɒdjuːl]=one of the units that together make a complete course, taught especially at a college or university=（尤指大学课程的）单元\n";
//
//    static String ww2 = "dermatologist\n" +
//            "trauma\n" +
//            "hormonally\n" +
//            "scalp\n" +
//            "temples\n" +
//            "receding\n" +
//            "crown=[kraʊn]=the top part of the head or a hat=（头、帽或山的）顶部\n" +
//            "follicles\n" +
//            "serum\n" +
//            "dropper\n" +
//            "moisturizes\n" +
//            "nourish\n" +
//            "preservatives\n" +
//            "cruelty free\n" +
//            "ethnicities\n" +
//            "pediatricians\n" +
//            "sulfates";

    static String ss1 = "5,6,7\n" +
            "15,16\n" +
            "18,19";

    static String ss2 = "1,2\n" +
            "6\n" +
            "18,19\n" +
            "21,22\n" +
            "31,32";



    public static String loadJSONFromAsset(Context context, String filePath) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static String readFileToString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static ArrayList<SrtItem> parseSrtJsonFile(Context context, String filePath) {
        String ss = readFileToString(filePath);
        ArrayList<SrtItem> list = GsonHelper.objectListFromString(ss, new TypeToken<ArrayList<SrtItem>>(){}.getType());
        Collections.sort(list, new Comparator<SrtItem>() {
            @Override
            public int compare(SrtItem o1, SrtItem o2) {
                if(o1.getIndex() > o2.getIndex()){
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });

        return list;
    }

    public static void parseContent() {
        parseWordsToJson(ww1);
//        parseImitateToJson(ss1);
//        parseImitateToJson(ss2);
    }

    static void parseImitateToJson(String wordStr) {
        String sectionArray[] = wordStr.split("\n");

        List<String> myList = new ArrayList();
        Collections.addAll(myList, sectionArray);


        List<ImitateSession> imitateSessions = new ArrayList<>();


        for(String imitateSession : myList) {

            String imitateStrArray[] = imitateSession.split(",");

            ImitateSession session = new ImitateSession();
            List<ImitateItem> imitateItems = new ArrayList<>();

            for(int i = 0; i < imitateStrArray.length; i++){

                String imitateItemStr = imitateStrArray[i];

                int index = Integer.parseInt(imitateItemStr);
                if(index > 0){
                    ImitateItem imitateItem = new ImitateItem();
                    imitateItem.setSubtitleIndex(index);
                    imitateItems.add(imitateItem);
                }
            }
            if(imitateItems.size() > 0){
                session.setImitateItems(imitateItems);
                imitateSessions.add(session);
            }
        }

        String json = new Gson().toJson(imitateSessions);
        Log.d("ParseWords",json);
    }

    static Locale[] dictLanCodesByOrder = {Locale.ENGLISH,
            Locale.SIMPLIFIED_CHINESE};

    static void parseWordsToJson(String wordStr) {
        String myArray[] = wordStr.split("\n");

        List<String> myList = new ArrayList();
        Collections.addAll(myList, myArray);

        List<WordItem> wordItems = new ArrayList<>();
        for(String wordLine : myList) {
            WordItem wordItem = new WordItem();
            List<LocalTranslatedItem> translatedItems = new ArrayList<>();

            String wordLineArray[] = wordLine.split("=");
            for(int i = 0; i < wordLineArray.length; i++){
                String line = wordLineArray[i];
                if(line.length() > 0){
                    if(0 == i){
                        wordItem.setMatchStr(line);
                    }
                    else if(1 == i && line.startsWith("[") && line.endsWith("]")) {
                        wordItem.setPhoneticStr(line);
                    }
                    else {
                        if(translatedItems.size() < dictLanCodesByOrder.length){
                            LocalTranslatedItem translatedItem = new LocalTranslatedItem();
                            String lanTag = dictLanCodesByOrder[translatedItems.size()].toLanguageTag();
                            translatedItem.setLanguageCode(lanTag);
                            translatedItem.setLocalStr(line);
                            translatedItems.add(translatedItem);
                        }
                    }
                }
                if(translatedItems.size() > 0){
                    wordItem.setTranslatedItems(translatedItems);
                }
            }
            if(wordItem.getMatchStr() != null){
                wordItems.add(wordItem);
            }
        }
        String json = new Gson().toJson(wordItems);
        Log.d("ParseWords",json);
    }

}

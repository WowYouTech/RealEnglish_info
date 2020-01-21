package com.youdao.sdk.ydtranslatedemo;

/**
 * @(#)BrowserActivity.java, 2013年10月22日. Copyright 2013 Yodao, Inc. All
 * rights reserved. YODAO PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.youdao.localtransengine.LanguageConvert;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydtranslate.EnWordTranslator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;
import com.youdao.sdk.ydtranslate.TranslatorOffline;
import com.youdao.sdk.ydtranslatedemo.utils.SwListDialog;
import com.youdao.sdk.ydtranslatedemo.utils.ToastUtils;
import com.youdao.sdk.ydtranslate.EnLineNMTTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lukun
 */
public class TranslateOfflineActivity extends Activity {

    // 评论列表
    private ListView questList;

    private TranslateAdapter adapter;

    private List<TranslateData> list = new ArrayList<TranslateData>();
    private List<Translate> trslist = new ArrayList<Translate>();
    TextView languageSelectFrom;
    TextView languageSelectTo;

    TextView languageSelectFrom1;
    TextView languageSelectTo1;

    TextView languageSelectFrom2;
    TextView languageSelectTo2;

    TextView languageSelectFrom3;
    TextView languageSelectTo3;

    private EditText fanyiInputText;
    private EditText fanyiInputText1;
    private EditText fanyiInputText2;
    private EditText fanyiInputText3;

    private InputMethodManager imm;
    Handler handler = new Handler();

    boolean tag = false;
    private TranslatorOffline translatorOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_OK);
        try {
            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
                    Window.PROGRESS_VISIBILITY_ON);
        } catch (Exception e) {
        }
        setContentView(R.layout.translate_list_offline);

        fanyiInputText = (EditText) findViewById(R.id.fanyiInputText);
        fanyiInputText1 = (EditText) findViewById(R.id.fanyiInputsText1);
        fanyiInputText2 = (EditText) findViewById(R.id.fanyiInputsText2);
        fanyiInputText3 = (EditText) findViewById(R.id.fanyiInputsText3);

        questList = (ListView) findViewById(R.id.commentList);

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        View view = this.getLayoutInflater().inflate(R.layout.translate_head,
                questList, false);
        questList.addHeaderView(view);
        adapter = new TranslateAdapter(this, list, trslist);

        questList.setAdapter(adapter);

        languageSelectFrom = (TextView) findViewById(R.id.languageSelectFrom);
        languageSelectFrom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectFrom);
            }
        });
        languageSelectTo = (TextView) findViewById(R.id.languageSelectTo);
        languageSelectTo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectTo);
            }
        });

        languageSelectFrom1 = (TextView) findViewById(R.id.languageSelectFrom1);
        languageSelectFrom1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectFrom1);
            }
        });
        languageSelectTo1 = (TextView) findViewById(R.id.languageSelectTo1);
        languageSelectTo1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectTo1);
            }
        });

        languageSelectFrom2 = (TextView) findViewById(R.id.languageSelectFrom2);
        languageSelectFrom2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectFrom2);
            }
        });
        languageSelectTo2 = (TextView) findViewById(R.id.languageSelectTo2);
        languageSelectTo2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectTo2);
            }
        });

        languageSelectFrom3 = (TextView) findViewById(R.id.languageSelectFrom3);
        languageSelectFrom3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectFrom3);
            }
        });
        languageSelectTo3 = (TextView) findViewById(R.id.languageSelectTo3);
        languageSelectTo3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectLanguage(languageSelectTo3);
            }
        });

        final long start = System.currentTimeMillis();
        translatorOffline = new TranslatorOffline();
        //(1)若使用离线查词，请通过方式一、方式二或者方式三初始化词库（方式一、二、三不可以共用）；
        //(2)若使用离线句子查询，请初始化离线句子库；
        //(3)若查词和查句子都初始化了，则每次查询，先去词库中找，然后去句子库中查询
        //初始化词库方式一，词库在assets/dict目录下,不支持直接放在assets目录下，会遍历该目录下所有词库进行初始化
//        translatorOffline.initWordDict("dict", true, new EnWordTranslator.EnWordInitListener() {
//            @Override
//            public void success() {
//
//            }
//            public void fail(TranslateErrorCode errorCode){
//                Log.d("translatorOffline","init false");
//            }
//        });
        //初始化词库方式二，指定词库绝对路径，会遍历该目录下所有词库进行初始化
        //"/data/oppo/coloros/youdao11111/"
        translatorOffline.initWordDict(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data", false, new EnWordTranslator.EnWordInitListener() {
            @Override
            public void success() {
                Log.d("translatorOffline", "init success");
                long end = System.currentTimeMillis();
                long time = end - start;
                Log.i("1111111111111111", "111111111初始化时间" + time);
            }

            public void fail(TranslateErrorCode errorCode) {
                Log.d("translatorOffline", "init false");
            }
        });

//        //初始化词库方式三，指定语种和路径
//        translatorOffline.initWordDictWithWordConvert(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Youdao/localdict/", false, WordConvert.EN2CH, new EnWordTranslator.EnWordInitListener() {
//            @Override
//            public void success() {
//            }
//            public void fail(TranslateErrorCode errorCode){
//
//            }
//        });
//
//        translatorOffline.initWordDictWithWordConvert("dict", true, WordConvert.CH2JA, new EnWordTranslator.EnWordInitListener() {
//            @Override
//            public void success() {
//            }
//            public void fail(TranslateErrorCode errorCode){
//
//            }
//        });
//
//        translatorOffline.initWordDictWithWordConvert(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Youdao/localdict2/", false, WordConvert.CH2KO, new EnWordTranslator.EnWordInitListener() {
//            @Override
//            public void success() {
//            }
//            public void fail(TranslateErrorCode errorCode){
//            }
//        });
//

        //比较耗时，建议线程调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化离线句子库
                translatorOffline.initLineDict(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/");
                translatorOffline.initLineLanguage(LanguageConvert.CH2EN, new EnLineNMTTranslator.EnLineInitListener() {
                    @Override
                    public void success() {
                    }

                    @Override
                    public void fail(TranslateErrorCode errorCode) {

                    }
                });
            }
        }).start();


        //获取词库是否初始化成功
//        translatorOffline.isLineInited();
//        translatorOffline.isWordInited();

    }

    private void selectLanguage(final TextView languageSelect) {
        List<String> items = new ArrayList<String>();
        items.add("中文");
        items.add("英文");
        items.add("日文");
        items.add("韩文");
        items.add("法文");
        items.add("西班牙文");
        items.add("越南语");
        items.add("印地文");

        SwListDialog exitDialog = new SwListDialog(TranslateOfflineActivity.this,
                items);
        exitDialog.setItemListener(new SwListDialog.ItemListener() {

            @Override
            public void click(int position, String title) {
                languageSelect.setText(title);
            }
        });
        exitDialog.show();
    }


    TranslateListener listener = new TranslateListener() {
        @Override
        public void onResult(final Translate result, String input, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TranslateData td = new TranslateData(
                            System.currentTimeMillis(), result);
                    list.add(td);
                    trslist.add(result);
                    adapter.notifyDataSetChanged();
                    questList.setSelection(list.size() - 1);
                    imm.hideSoftInputFromWindow(
                            fanyiInputText.getWindowToken(), 0);
                }
            });
        }

        @Override
        public void onError(final TranslateErrorCode error, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("查询错误:" + error.name());
                }
            });
        }

        @Override
        public void onResult(final List<Translate> results, List<String> inputs, final List<TranslateErrorCode> errors, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder("错误如下：");
                    boolean error = false;
                    for (int i = 0; i < results.size(); i++) {
                        Translate result = results.get(i);
                        if (result == null) {
                            sb.append(i + " " + errors.get(i).getCode() + "  ");
                            error = true;
                            continue;
                        }
                        TranslateData td = new TranslateData(
                                System.currentTimeMillis(), result);
                        list.add(td);
                        trslist.add(result);
                        adapter.notifyDataSetChanged();
                        questList.setSelection(list.size() - 1);
                        imm.hideSoftInputFromWindow(
                                fanyiInputText.getWindowToken(), 0);
                    }
                    if (error) {
                        ToastUtils.show(sb.toString());
                    }
                }
            });
        }
    };

    public void unVisuable(View view) {
        View r = findViewById(R.id.translateInputsBar);
        r.setVisibility(View.GONE);
    }

    public void query(View view) {
        final String input = fanyiInputText.getText().toString();
        if (TextUtils.isEmpty(input)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("请输入要查询的词");
                }
            });
        }
        String from = languageSelectFrom.getText().toString();
        String to = languageSelectTo.getText().toString();
        Language langFrom = LanguageUtils.getLangByName(from);
        Language langTo = LanguageUtils.getLangByName(to);
        final TranslateParameters parameters = new  TranslateParameters.Builder().
                from(langFrom).to(langTo).useAutoConvertWord(false).useAutoConvertLine(false).build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                translatorOffline.lookupNative(input, parameters, "requestId", listener);
            }
        }).start();
    }

    public void querys(View view) {
        final String input1 = fanyiInputText1.getText().toString();
        final String input2 = fanyiInputText2.getText().toString();
        final String input3 = fanyiInputText3.getText().toString();

        if (TextUtils.isEmpty(input1) || TextUtils.isEmpty(input2) || TextUtils.isEmpty(input3)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("请输入要查询的词");
                }
            });
        }
        String from1 = languageSelectFrom1.getText().toString();
        String to1 = languageSelectTo1.getText().toString();
        Language langFrom1 = LanguageUtils.getLangByName(from1);
        Language langTo1 = LanguageUtils.getLangByName(to1);
        final TranslateParameters parameters1 = new TranslateParameters.Builder().
                from(langFrom1).to(langTo1).useAutoConvertWord(false).useAutoConvertLine(false).build();

        String from2 = languageSelectFrom2.getText().toString();
        String to2 = languageSelectTo2.getText().toString();
        Language langFrom2 = LanguageUtils.getLangByName(from2);
        Language langTo2 = LanguageUtils.getLangByName(to2);
        final TranslateParameters parameters2 = new TranslateParameters.Builder().
                from(langFrom2).to(langTo2).useAutoConvertWord(false).useAutoConvertLine(false).build();

        String from3 = languageSelectFrom3.getText().toString();
        String to3 = languageSelectTo3.getText().toString();
        Language langFrom3 = LanguageUtils.getLangByName(from3);
        Language langTo3 = LanguageUtils.getLangByName(to3);
        final TranslateParameters parameters3 = new TranslateParameters.Builder().
                from(langFrom3).to(langTo3).useAutoConvertWord(false).useAutoConvertLine(false).build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> inputs = new ArrayList<String>();
                inputs.add(input1);
                inputs.add(input2);
                inputs.add(input3);

                List<TranslateParameters> p = new ArrayList<TranslateParameters>();
                p.add(parameters1);
                p.add(parameters2);
                p.add(parameters3);
                translatorOffline.lookupNativeWithList(inputs, p, "requestId", listener);
            }
        }).start();
    }

    public void loginBack(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        translatorOffline.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void finish() {
        super.finish();
    }

}

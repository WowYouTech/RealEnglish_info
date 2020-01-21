package com.youdao.sdk.ydtranslatedemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.youdao.sdk.chdict.ChDictTranslate;
import com.youdao.sdk.chdict.ChDictor;
import com.youdao.sdk.chdict.ChdictMeans;
import com.youdao.sdk.chdict.DictListener;
import com.youdao.sdk.chdict.ExamLine;
import com.youdao.sdk.ydtranslatedemo.utils.ToastUtils;

import java.util.List;

public class HanyuActivity extends Activity {
    private ChDictor chDictor = null;

    private EditText editText;

    private Button startBtn;

    private TextView textView, moreBtn;

    private Handler handler = new Handler();

    List<ChDictTranslate> chTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hanyu);
        editText = (EditText) findViewById(R.id.editext);
        startBtn = (Button) findViewById(R.id.startBtn);
        textView = (TextView) findViewById(R.id.textView);
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lookup();
            }

        });
        moreBtn = (TextView) findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                //sdk自动封装了deeplink调用逻辑
                chTs.get(0).openMore(HanyuActivity.this);
                //注意，若用户没安装有道词典，开发者可自己实现deeplink的跳转
//                String texts = editText.getText().toString();
//                if(!chTs.get(0).openDict(HanyuActivity.this)){
//                	 //获取deeplink链接
//                    String deeplinkUrl = chTs.get(0).getDictWebUrl();
//                    //处理deeplink链接，可通过自定义浏览器打开
//                    TranslateForwardHelper.toBrowser(HanyuActivity.this, deeplinkUrl);
//                }
            }
        });

    }

    private void lookup() {
        // TODO Auto-generated method stub
        // 注意，每次查询之前都需要初始化
        moreBtn.setVisibility(View.GONE);
        final String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            ToastUtils.show("请输入要查询的词");
            return;
        }
        if (chDictor == null) {
            /*
             * 请确保设置的路径下包含汉语词典的离线包
             */
//            //设置离线句子库位置，确保Environment.getExternalStorageDirectory()+/update路径
            chDictor = new ChDictor(Environment.getExternalStorageDirectory()+"/update", false);

            //放置在assets目录下
//            chDictor = new ChDictor("dict", true);//放在assets/dict下
        }
        try {
            chDictor.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        chDictor.lookup(text, new DictListener() {

            @Override
            public void onResult(final List<ChDictTranslate> chTs) {
                // 注意：回调是在子线程中进行，拿到查询结果之后若有更新UI操作，请切换到主线程执行
                HanyuActivity.this.chTs = chTs;
                handler.post(new Runnable() {
                    public void run() {
                        showResult(chTs);
                    }
                });
            }

            @Override
            public void onError(String q, String info) {

            }
        });

    }

    public void showResult(final List<ChDictTranslate> chTs) {

        String text = "查询失败";
        StringBuilder sb = new StringBuilder();
        for (ChDictTranslate chT : chTs) {
            if (chT.success()) {
                sb.append("<p>单词：" + chT.getQuery() + "<br/>");
                sb.append("<p>发音：" + chT.getPhone() + "<br/>");
                List<ChdictMeans> list = chT.getTranslations();
                if (list != null) {
                    sb.append("<p>含义如下：");
                    for (ChdictMeans chdictMeans : list) {
                        String trs = chdictMeans.getTranslate();
                        sb.append("<br/><br/>解释：" + trs
                                + "<br/>例句：");
                        List<ExamLine> lineList = chdictMeans
                                .getExamLines();
                        if (lineList == null) {
                            continue;
                        }
                        for (ExamLine line : lineList) {
                            String texts = line.getText();
                            String span = null;
                            if (line.isHighlight()) {
                                span = "<span><font color=\"#ff0000\">"
                                        + texts
                                        + "</font></span>";
                            } else {
                                span = "<span><font color=\"#808080\">"
                                        + texts
                                        + "</font></span>";
                            }
                            sb.append(span);
                        }
                    }
                }
                sb.append("</p>");
                moreBtn.setVisibility(View.VISIBLE);
            }
        }
        text = sb.toString();
        textView.setText(Html.fromHtml(text));

    }

    public void loginBack(View view) {
        this.finish();
    }

}


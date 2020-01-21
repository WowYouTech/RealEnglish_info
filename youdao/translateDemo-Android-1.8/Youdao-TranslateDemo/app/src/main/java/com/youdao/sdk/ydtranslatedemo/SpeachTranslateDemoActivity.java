package com.youdao.sdk.ydtranslatedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youdao.sdk.app.EncryptHelper;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.common.Constants;
import com.youdao.sdk.ydonlinetranslate.SpeechTranslateParameters;
import com.youdao.sdk.ydonlinetranslate.SpeechTranslate;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;
import com.youdao.sdk.ydtranslatedemo.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SpeachTranslateDemoActivity extends Activity {

    private TextView resultText;
    private TextView filePathText;
    private Button toDetail;
    private File audioFile;
    private Translate tr = null;
    ExtAudioRecorder recorder;
    SpeechTranslateParameters tps;
    Handler handler = new Handler();

    /**
     * 录音失败的提示
     */
    ExtAudioRecorder.RecorderListener listener = new ExtAudioRecorder.RecorderListener() {
        @Override
        public void recordFailed(int failRecorder) {
            if (failRecorder == 0) {
                Toast.makeText(SpeachTranslateDemoActivity.this, "录音失败，可能是没有给权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SpeachTranslateDemoActivity.this, "发生了未知错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voicerecognize_demo);

        resultText = (TextView) findViewById(R.id.shibietext);
        filePathText = (TextView) findViewById(R.id.filepath);
        toDetail = (Button) findViewById(R.id.detailBtn);
    }

    public void select(View view) {
        Intent intent = new Intent();
        intent.setType("audio/*"); //选择音频
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void record(View view) {
        try {
            audioFile = File.createTempFile("record_", ".wav");
            AuditRecorderConfiguration configuration = new AuditRecorderConfiguration.Builder()
                    .recorderListener(listener)
                    .handler(handler)
                    .rate(Constants.RATE_16000)
                    .uncompressed(true)
                    .builder();

            recorder = new ExtAudioRecorder(configuration);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordstop(View view) {
        try {
            int time = recorder.stop();
            if (time > 0) {
                if (audioFile != null) {
                    filePathText.setText(audioFile.getAbsolutePath());
                }
            }
            recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void todetail(View view) {
        if (tr == null) {
            ToastUtils.show(this, "请先识别");
            return;
        }
        TranslateData td = new TranslateData(
                System.currentTimeMillis(), tr);
        TranslateDetailActivity.open(this, td, tr);

    }

    public void recognize(View view) {
        final String text = (String) filePathText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(SpeachTranslateDemoActivity.this, "请录音或选择音频文件", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        try {
            resultText.setText("正在识别，请稍等....");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startRecognize(text);
                }
            }).start();
        } catch (Exception e) {
        }
    }

    private void startRecognize(String filePath) {
        byte[] datas = null;
        try {
            datas = FileUtils.getContent(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String bases64 = EncryptHelper.getBase64(datas);

        //输入和输出音频格式都为wav格式
        tps = new SpeechTranslateParameters.Builder().source("youdaovoicetranslate")
                .from(Language.ENCH).to(Language.ENGLISH)
                .rate(Constants.RATE_16000)//输入音频码率，支持8000,16000
                .voice(Constants.VOICE_NEW)//输出声音，支持美式女生、美式男生、英式女生、英式男生
                .timeout(100000)//超时时间
                .build();

        SpeechTranslate.getInstance(tps).lookup(bases64,"requestId",
                new TranslateListener() {
                    @Override
                    public void onResult(final Translate result, String input, String requestId) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultText.setText("翻译完成:" + result.getQuery());
                                tr = result;
                                toDetail.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onError(final TranslateErrorCode error, String requestId) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tr = null;
                                resultText.setText("翻译失败" + error.toString());
                                toDetail.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onResult(List<Translate> results, List<String> inputs, List<TranslateErrorCode> errors, String requestId) {

                    }
                });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                filePathText.setText(FileUtils.getPath(this, uri));
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

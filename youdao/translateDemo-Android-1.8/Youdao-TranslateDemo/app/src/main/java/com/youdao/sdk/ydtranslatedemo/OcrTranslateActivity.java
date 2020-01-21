package com.youdao.sdk.ydtranslatedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youdao.sdk.app.EncryptHelper;
import com.youdao.sdk.ydonlinetranslate.LanguageOcrTranslate;
import com.youdao.sdk.ydonlinetranslate.OCRTranslateResult;
import com.youdao.sdk.ydonlinetranslate.OcrTranslate;
import com.youdao.sdk.ydonlinetranslate.OcrTranslateListener;
import com.youdao.sdk.ydonlinetranslate.OcrTranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Region;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by baijing on 2017/7/3.
 */

public class OcrTranslateActivity extends Activity {
    TextView original;
    Uri currentUri;
    String filePath;
    ImageView imageView;
    BorderTextView borderTextView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_demo);
        imageView = (ImageView) findViewById(R.id.imageView);
        original = (TextView) findViewById(R.id.original);
        borderTextView = (BorderTextView) findViewById(R.id.resultText);
    }

    OcrTranslateListener translateListener = new OcrTranslateListener() {
        @Override
        public void onError(final TranslateErrorCode error, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    original.setText("翻译失败" + error.toString());
                }
            });
        }

        @Override
        public void onResult(final OCRTranslateResult result, String input, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    original.setText("翻译完成:");
                    String text = getResult(result);
                    SpannableString spannableString = new SpannableString(text);
                    int start = 0;
                    while (start < text.length() && start >= 0) {
                        int s = text.indexOf("段落", start);
                        int end = text.indexOf("：", s) + 1;
                        if (s >= 0) {
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#808080"));
                            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(35);
                            UnderlineSpan underlineSpan = new UnderlineSpan();
                            spannableString.setSpan(sizeSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            spannableString.setSpan(colorSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            spannableString.setSpan(underlineSpan, s, end - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            start = end;
                        } else {
                            break;
                        }
                    }
                    borderTextView.setVisibility(View.VISIBLE);
                    borderTextView.setText(spannableString);
                }
            });
        }
    };

    public void takePhoto(View view) {
        String state = Environment.getExternalStorageState(); // 判断是否存在sd卡
        if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
            Intent intent = new Intent(
                    "android.media.action.IMAGE_CAPTURE");
            filePath = getFileName();
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, "com.youdao.sdk.ydtranslatedemo.fileprovider", new File(filePath));
            } else {
                uri = Uri.fromFile(new File(filePath));
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    uri);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(OcrTranslateActivity.this, "请检查手机是否有SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName() {
        String saveDir = Environment.getExternalStorageDirectory() + "/myPic";
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir(); // 创建文件夹
        }
        // 用日期作为文件名，确保唯一性
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fileName = saveDir + "/" + formatter.format(date) + ".png";
        return fileName;
    }

    public void getImage(View view) {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 2);
    }

    public void recognize(View view) {
        if (currentUri == null) {
            Toast.makeText(OcrTranslateActivity.this, "请拍摄或选择图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Bitmap bitmap = ImageUtils.compressBitmap(OcrTranslateActivity.this, currentUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] datas = baos.toByteArray();
            String bases64 = EncryptHelper.getBase64(datas);
            int count = bases64.length();
            while (count > 1.4 * 1024 * 1024) {
                quality = quality - 10;
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                datas = baos.toByteArray();
                bases64 = EncryptHelper.getBase64(datas);
            }
            startTranslate(bases64);
        } catch (Exception e) {
        }
    }

    public void startTranslate(final String base64) {
        OcrTranslateParameters ocrP = new OcrTranslateParameters.Builder().timeout(6000).from(LanguageOcrTranslate.AUTO).to(LanguageOcrTranslate.CHINESE).build(); //zh-en
        OcrTranslate.getInstance(ocrP).lookup(base64, "requestid", translateListener);
        Toast.makeText(OcrTranslateActivity.this, "开始识别，请稍等~~~", Toast.LENGTH_LONG).show();
    }

    private String getResult(OCRTranslateResult result) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        //按文本识别
        List<Region> regions = result.getRegions();
        for (Region region : regions) {
            sb.append("段落" + i++ + "：\n");
            sb.append(region.getContext()).append("\n");
            sb.append("翻译：");
            sb.append(region.getTranContent()).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    //得到照片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            if (uri == null && !TextUtils.isEmpty(filePath)) {
                uri = Uri.parse(filePath);
            }
            Log.e("uri", uri.toString());
            Bitmap bitmap = ImageUtils.compressBitmap(this, uri);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.postInvalidate();
            }
            currentUri = uri;
            imageView.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

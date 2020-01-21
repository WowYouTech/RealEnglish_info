package com.youdao.sdk.ocrdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logic.ImgFileListActivity;
import com.youdao.ocr.online.ImageOCRecognizer;
import com.youdao.ocr.online.Line;
import com.youdao.ocr.online.Line_Line;
import com.youdao.ocr.online.OCRListener;
import com.youdao.ocr.online.OCRParameters;
import com.youdao.ocr.online.OCRResult;
import com.youdao.ocr.online.OcrErrorCode;
import com.youdao.ocr.online.RecognizeLanguage;
import com.youdao.ocr.online.Region;
import com.youdao.ocr.online.Region_Line;
import com.youdao.ocr.online.Word;
import com.youdao.sdk.app.EncryptHelper;

public class OcrDemoActivity extends Activity {

    // private ImageView imageView;

    private TextView resultText;

    ArrayList<ShibieEntivity> shibieEntivitys;

    String filePath;

    Handler handler = new Handler();

    ListView shibielist;

    ShibieAdapter adapter;

    OCRParameters tps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_demo);

        shibielist = (ListView) findViewById(R.id.shibielist);

        View header = getLayoutInflater().inflate(R.layout.shibieheader, null);
        resultText = (TextView) header.findViewById(R.id.shibietext);

        shibieEntivitys = new ArrayList<ShibieEntivity>();
        adapter = new ShibieAdapter(this, shibieEntivitys);

        shibielist.addHeaderView(header);
        shibielist.setAdapter(adapter);


        Intent in = getIntent();
        ArrayList<String> list = in.getStringArrayListExtra("files");
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShibieEntivity entertity = new ShibieEntivity();
                entertity.setUri(Uri.parse(list.get(i)));
                shibieEntivitys.add(entertity);
            }
            adapter.notifyDataSetChanged();
        }
        tps = new OCRParameters.Builder().source("youdaoocr").timeout(100000)
                .type(OCRParameters.TYPE_LINE).lanType(RecognizeLanguage.LINE_CHINESE_ENGLISH.getCode()).build();//
        //默认按行识别，支持自动、中英繁、日韩、拉丁、印地语，其中自动识别不支持印地语识别，其他都可以
        // 当采用按字识别时，识别语言支持中英和英文识别，其中"zh-en"为中英识别，"en"参数表示只识别英文。若为纯英文识别，"zh-en"的识别效果不如"en"，请妥善选择
    }

    public void selectPhote(View view) {
        Intent intent = new Intent();
        intent.setClass(OcrDemoActivity.this, ImgFileListActivity.class);
        startActivity(intent);
        finish();
    }

    public void takePhote(View view) {
        String state = Environment.getExternalStorageState(); // 判断是否存在sd卡
        if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
            Intent intent = new Intent(
                    "android.media.action.IMAGE_CAPTURE");
            filePath = getFileName();
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, "com.youdao.sdk.ocrdemo.fileprovider", new File(filePath));
            } else {
                uri = Uri.fromFile(new File(filePath));
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    uri);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(OcrDemoActivity.this, "请检查手机是否有SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void recognize(View view) {
        if (shibieEntivitys == null || shibieEntivitys.size() == 0) {
            Toast.makeText(OcrDemoActivity.this, "请拍摄或选择图片", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < shibieEntivitys.size(); i++) {
                        startRecognize(i);
                    }
                }
            }).start();
        } catch (Exception e) {
        }
    }

    private void startRecognize(final int i) {
        final Bitmap bitmap = ImageUtils.readBitmapFromFile(
                shibieEntivitys.get(i).getUri().getPath(), 768);

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
        final String base64 = bases64;
        handler.post(new Runnable() {
            @Override
            public void run() {
                resultText.setText("识别中....");
            }
        });
        ImageOCRecognizer.getInstance(tps).recognize(base64,
                new OCRListener() {
                    @Override
                    public void onResult(final OCRResult result, String input) {
                        //若有更新界面操作，请切换到主线程
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String text = getResult(result);
                                SpannableString spannableString = new SpannableString(text);
                                int start = 0;
                                while(start < text.length() && start >= 0){
                                    int s = text.indexOf("文本", start);
                                    int end = text.indexOf("：", s) + 1;
                                    if(s >= 0){
                                        ForegroundColorSpan  colorSpan = new ForegroundColorSpan (Color.parseColor("#808080"));
                                        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan (35);
                                        UnderlineSpan underlineSpan = new UnderlineSpan();
                                        spannableString.setSpan(sizeSpan, s,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        spannableString.setSpan(colorSpan, s,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        spannableString.setSpan(underlineSpan, s,end - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        start = end;
                                    }else{
                                        break;
                                    }

                                }

                                shibieEntivitys.get(i).setText(spannableString);
                                resultText.setText("识别完成");
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(final OcrErrorCode error) {
                        // resultText.setText("识别失败");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                resultText.setText("识别失败" + error.name());
                            }
                        });
                    }
                });
    }

    private String getResult(OCRResult result) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        if(OCRParameters.TYPE_TEXT.equals(result.getType())){
            //按文本识别
            List<Region> regions = result.getRegions();
            for (Region region : regions) {
                List<Line> lines = region.getLines();
                for (Line line : lines) {
                    sb.append("文本"+ i++ + "： ");
                    List<Word> words = line.getWords();
                    for (Word word : words) {
                        sb.append(word.getText()).append(" ");
                    }
                    sb.append("\n");
                }
            }
        }else{
            //按行识别
            List<Region_Line> regions = result.getRegions_Line();
            for (Region_Line region : regions) {
                List<Line_Line> lines = region.getLines();
                for (Line_Line line : lines) {
                    sb.append("文本"+ i++ + "： ");
                    sb.append(line.getText());
                    sb.append("\n");
                }
            }
        }
        String text = sb.toString();
        if(!TextUtils.isEmpty(text)){
            text = text.substring(0, text.length() - 2);
        }
        return text;

    }

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
            if (uri == null) {
                return;
            }
            ShibieEntivity entertity = new ShibieEntivity();
            entertity.setUri(uri);
            shibieEntivitys.add(entertity);
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loginBack(View view) {
        this.finish();
    }

    /**
     * 生成文件路径和文件名
     *
     * @return
     */
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
}

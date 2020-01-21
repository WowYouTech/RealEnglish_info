package com.youdao.sdk.ydtranslatedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by baijing on 2018/3/30.
 */

public class BorderTextView extends TextView{

    public BorderTextView(Context context) {
        super(context);
    }
    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int sroke_width = 1;
    @Override
    protected void onDraw(Canvas canvas) {
        int w = this.getWidth();
        if(this.getHeight() > 5 && this.getWidth() > 5){
            Paint paint = new Paint();
            //  将边框设为黑色
            paint.setColor(Color.GRAY);
            //  画TextView的4个边
            canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
            canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
            canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
            canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
            super.onDraw(canvas);
        }
    }
}

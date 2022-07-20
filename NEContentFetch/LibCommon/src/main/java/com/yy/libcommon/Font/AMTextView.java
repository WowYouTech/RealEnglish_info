package com.yy.libcommon.Font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.yy.libcommon.R;


/**
 * Created by steve.yang on 16/11/16.
 */

public class AMTextView extends AppCompatTextView {
    public AMTextView(Context context) {
        super(context);

        applyCustomFont(context,null);
    }

    public AMTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context,attrs);
    }

    public AMTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context,attrs);
    }


    private void applyCustomFont(Context context, AttributeSet attrs) {

        String ttf;
        Typeface typeface = null;

        if(null != attrs){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AMTextView);
            ttf = a.getString(R.styleable.AMTextView_ttf_type);
            typeface = FontCache.getTypeFace(getContext(),ttf);
        }

        if(null == typeface){
            typeface = FontCache.getTypeFace(getContext(),"regular");
        }
        if(typeface != null){
            setTypeface(typeface);
        }

    }

}



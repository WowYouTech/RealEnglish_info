package com.youdao.sdk.ocrdemo;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ShibieAdapter extends BaseAdapter {
	ArrayList<ShibieEntivity> apk_list;
	LayoutInflater inflater;
	Context context;
	DisplayImageOptions options;
	ImageLoader imageLoader;

	public ShibieAdapter(Context context, ArrayList<ShibieEntivity> apk_list) {
		this.apk_list = apk_list;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		options = new DisplayImageOptions.Builder().build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return apk_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return apk_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ShibieEntivity entity = apk_list.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.shibierow, null);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.imageView);
			holder.text = (TextView) convertView.findViewById(R.id.resultText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String file = entity.getUri().toString();
		imageLoader.displayImage("file://" + file, holder.imageview, options);
		if(!TextUtils.isEmpty(entity.getText())){
			holder.text.setText(entity.getText());
			holder.text.setVisibility(View.VISIBLE);
		}else{
			holder.text.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageview;
		TextView text;
	}
}

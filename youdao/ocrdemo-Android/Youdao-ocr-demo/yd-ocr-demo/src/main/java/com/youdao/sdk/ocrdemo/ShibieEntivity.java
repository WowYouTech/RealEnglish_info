package com.youdao.sdk.ocrdemo;

import android.net.Uri;
import android.text.SpannableString;

public class ShibieEntivity {
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public SpannableString getText() {
		return text;
	}

	public void setText(SpannableString text) {
		this.text = text;
	}

	Uri uri;
	SpannableString text;
}

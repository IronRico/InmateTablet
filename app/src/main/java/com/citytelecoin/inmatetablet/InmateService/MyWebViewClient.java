package com.citytelecoin.inmatetablet.InmateService;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Only opens websites from the core website.
 * Doesn't allow Chrome to open during navigation
 */

public class MyWebViewClient extends WebViewClient {
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

		//only loads pages from the core website
		if(Uri.parse(url).getHost().contains("citytelecoin"))
		{
			view.loadUrl(url);
		}

		//Doesn't allow Chrome to open during navigation
		return true;
	}
}
package com.citytelecoin.inmatetablet.InmateService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.citytelecoin.inmatetablet.Apps.Apps;
import com.citytelecoin.inmatetablet.Apps.AppsAdmin;
import com.citytelecoin.inmatetablet.Login.Login;
import com.citytelecoin.inmatetablet.R;


public class InmateService extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener{

	private FrameLayout mWebContainer;
	private WebView myWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.inmateservice_sidebar_menu);

		//Toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

			@Override //closes keypad when drawer is open
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);

				InputMethodManager inputMethodManager =
						(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				assert inputMethodManager != null;
				//noinspection ConstantConditions
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			}
		};

		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		//Initializes WebView and set it to id webview in activity_main.xml in layout
		mWebContainer = findViewById(R.id.webview);
		myWebView = new WebView(getApplicationContext());
		mWebContainer.addView(myWebView);

		//WebView Settings
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setAllowFileAccess(true);
		webSettings.getDatabaseEnabled();

		//Set WebViewClient and ChromeClient
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.setWebChromeClient(new WebChromeClient(){
			// Need to gather permissions to use the camera and audio
			@Override
			public void onPermissionRequest(final PermissionRequest request) {
				InmateService.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {

						request.grant(request.getResources());
					}
				});
			}
		});

		//Initializes URL that will be loaded into WebView
		//private String url = "https://vidtest.citytelecoin.com/kiosk/";
		String url = "https://vidtest.citytelecoin.com/tablet/index.html";

		myWebView.loadUrl(url);
	}

	//closes sidebar when backbutton is pressed
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();


		if (id == R.id.apps) {
			Intent myIntent = new Intent(InmateService.this, Apps.class);
			startActivity(myIntent);
		}
		else if (id == R.id.logout) {
			Intent myIntent = new Intent(InmateService.this, Login.class);
			startActivity(myIntent);
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	//Destroys views
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebContainer.removeAllViews();
		myWebView.destroy();
	}
}

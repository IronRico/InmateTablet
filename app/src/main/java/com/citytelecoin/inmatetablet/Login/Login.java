package com.citytelecoin.inmatetablet.Login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.citytelecoin.inmatetablet.InmateService.InmateService;
import com.citytelecoin.inmatetablet.InmateService.InmateServiceAdmin;
import com.citytelecoin.inmatetablet.KioskMode.CustomViewGroup;
import com.citytelecoin.inmatetablet.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Login extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private CameraSource camera;
	private Preview nPreview;
	private FaceOverlay nFaceOverlay;
	private static final int PLAY_SERVICES = 9001;

	private EditText pin;
	private boolean isAdmin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_sidebar_menu);

		//Disables lock screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		disableStatusBar();

		//sidebar menu
		menu();

		//Preview screen
		nPreview = findViewById(R.id.preview);
		nFaceOverlay = findViewById(R.id.faceBox);

		//Creates Camera
		createCamera();

		//Listener class for Login Button
		loginButton();
	}

	//Listener for the Login Button
	public void loginButton() {

		pin = findViewById(R.id.pin);
		Button login = findViewById(R.id.submit);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pin.getText().toString().equals("1234")){
					isAdmin = true;
					camera.takePicture(null, JPG);
				}else {
					camera.takePicture(null, JPG);
				}

				/*if(nFaceOverlay.isFace()) {
					if(pin == correct)
						if(pin == 1234){
							isAdmin = true;
							camera.takePicture(null, jpeg);
						} else{
							camera.takePicture(null, jpeg);
						}

					}else{
						Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "No Face Detected", Toast.LENGTH_SHORT).show();
				}*/
			}
		});
	}

	//Disables status bar
	public void disableStatusBar(){
		WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		localLayoutParams.gravity = Gravity.TOP;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

				// this is to enable the notification to receive touch events
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

				// Draws over status bar
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		localLayoutParams.height = (int) (30 * getResources().getDisplayMetrics().scaledDensity);
		localLayoutParams.format = PixelFormat.TRANSPARENT;

		CustomViewGroup view = new CustomViewGroup(this);

		assert manager != null;
		manager.addView(view, localLayoutParams);
	}

	//Sidebar Menu
	public void menu(){
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
	}

	//When back is pressed and the sidebar is open, it collapses the side bar
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		//int id = item.getItemId();

		/*if (id == R.id.apps) {
			Intent myIntent = new Intent(Login.this, Apps.class);
			startActivity(myIntent);
		} else if (id == R.id.logout) {
			Intent myIntent = new Intent(Login.this, Login.class);
			startActivity(myIntent);
		}*/

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	//Restarts Camera
	@Override
	protected void onResume() {
		super.onResume();
		startCamera();
	}

	//Stops the preview
	@Override
	protected void onPause(){
		super.onPause();
		nPreview.stop();
	}

	//Destroys resources when closed and releases the camera
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(camera != null){
			camera.release();
		}
	}

	//Creates the Front Facing Camera
	private void createCamera(){

		Context appContext = getApplicationContext();
		com.google.android.gms.vision.face.FaceDetector detector = new FaceDetector.Builder(appContext)
				.setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
				.build();

		detector.setProcessor(new MultiProcessor.Builder<>(new FaceTracker()).build());

		camera = new CameraSource.Builder(appContext, detector)
				.setRequestedPreviewSize(650,480)
				.setFacing(CameraSource.CAMERA_FACING_FRONT)
				.setRequestedFps(30.0f)
				.build();
	}

	//Starts the Camera for the Preview
	private void startCamera(){

		int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
		if (code != ConnectionResult.SUCCESS) {
			Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, PLAY_SERVICES);
			dlg.show();
		}

		if(camera != null){
			try{
				nPreview.startRequest(camera, nFaceOverlay);
			} catch (IOException e) {
				camera.release();
				camera = null;
			}
		}
	}

	//Create multiple face trackers for each detected face
	private class FaceTracker implements  MultiProcessor.Factory<Face> {
		@Override
		public Tracker<Face> create (Face face) {
			return new GraphicTracker(nFaceOverlay);
		}
	}

	//Maintains graphic for each detected face
	private class GraphicTracker extends Tracker<Face> {

		private FaceOverlay graphic;
		private RenderFace rFace;

		GraphicTracker(FaceOverlay overlay){
			graphic = overlay;
			rFace = new RenderFace(overlay);
		}

		//starts tracking new face
		@Override
		public void onNewItem(int faceId, Face item){

			graphic.setId(faceId);
		}

		//Updates Position
		@Override
		public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
			graphic.add(rFace);
			rFace.updateFace(face);
		}

		//Hide graphic when face is temporarily missing from preview
		@Override
		public void onMissing(FaceDetector.Detections<Face> detectionResults) {
			graphic.remove(rFace);
		}

		//Remove graphic when face permanently removed from preview
		@Override
		public void onDone(){
			graphic.remove(rFace);
		}
	}

	//PictureCallBack for takePicture method
	CameraSource.PictureCallback JPG = new CameraSource.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data) {
			new saveImage().execute(data);

			if(isAdmin){
				//Navigates to InmateServiceAdmin
				Intent myIntent = new Intent(Login.this, InmateServiceAdmin.class);
				startActivity(myIntent);
			} else{
				//Navigates to InmateService
				Intent myIntent = new Intent(Login.this, InmateService.class);
				startActivity(myIntent);
			}

		}
	};

	@SuppressLint("StaticFieldLeak")
	private class saveImage extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream;

			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/ctc");
				dir.mkdir();

				@SuppressLint("DefaultLocale")
				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				refreshGallery(outFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

}

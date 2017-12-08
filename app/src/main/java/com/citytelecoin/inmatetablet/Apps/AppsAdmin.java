package com.citytelecoin.inmatetablet.Apps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.citytelecoin.inmatetablet.Login.Login;
import com.citytelecoin.inmatetablet.R;

//Lists installed apps on the device

public class AppsAdmin extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_sidebar_menu);

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

		loadApplication();
		loadListView();
		Click();
	}

	//closes sidebar when back button is pressed
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	//creates Settings menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//Open Settings
		if (id == R.id.action_settings) {
			AlertDialog.Builder StatusDialogBuilder = new AlertDialog.Builder(context);

			// set title
			StatusDialogBuilder.setTitle("Enter Admin Password");
			// Setting an EditText view to get user input
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input.setTransformationMethod(new PasswordTransformationMethod());
			StatusDialogBuilder.setView(input);

			//set dialog message
			//Note the hard coded password of "1234" in the if input.getText portion below.
			//This should be changed to something more secure on a backend reception.
			//While unlikely that an inmate would get access to final source code, it would just be better security practice.

			StatusDialogBuilder.setMessage("Password Required").setCancelable(false).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					if (input.getText().toString().equals("1234")) {
						startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
					} else {
						Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
					}

				}

			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = StatusDialogBuilder.create();
			// show it
			alertDialog.show();


			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.logout) {
			Intent myIntent = new Intent(AppsAdmin.this, Login.class);
			startActivity(myIntent);
		} /*else if (id == R.id.apps){
			Intent myIntent = new Intent(Apps.this, Apps.class);
			startActivity(myIntent);
		}*/

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	//Code to stop long power press from displaying shut down option
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus) {
			// Close every kind of system dialog
			Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(closeDialog);
		}
	}

	public class AppDetail {
		CharSequence label;
		CharSequence name;
		Drawable icon;
	}



	//Building the code to determine what apps to display: tutsplus is great!
	//In the loadApps method of the AppsListActivity class,
	// we use the queryIntentActivities method of the PackageManager
	// class to fetch all the Intents that have a category of Intent.CATEGORY_LAUNCHER.
	// The query returns a list of the applications that can be launched by a launcher.
	// We loop through the results of the query and add each item to a list named apps.
	//Note the filter for system apps included from the isSystemPackage boolean
	//underneath the if/else statement as well


	private PackageManager manager;
	private List<AppDetail> apps;
	private void loadApplication() {
		manager = getPackageManager();
		apps = new ArrayList<>();
		Intent Main = new Intent(Intent.ACTION_MAIN, null);
		Main.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = manager.queryIntentActivities(Main, 0);
		for (ResolveInfo ri : resolveInfos) {
			String packageName = ri.activityInfo.packageName;
			Log.i("Package Name", packageName);

			if(!packageName.contains("com.citytelecoin") && !packageName.contains("com.example") && !isSystemPackage(ri)){
				AppDetail app = new AppDetail();
				app.label = ri.loadLabel(manager);
				app.name = ri.activityInfo.packageName;
				app.icon = ri.activityInfo.loadIcon(manager);
				apps.add(app);
			}
		}
	}

	//This boolean allows me to use the system app filter as see
	// in the above if/else statement where isSystemPackage is called
	private boolean isSystemPackage(ResolveInfo resolveInfo) {
		return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}


	//This code helps to build the visual aspect of the app list
	//Note how it takes teh app.X details from above code.
	//Note the created ArrayAdapter and override its getView method to render the list's items.
	// We then associate the ListView with the adapter.

	private ListView list;
	private void loadListView(){
		list = findViewById(R.id.apps_list);
		ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
				R.layout.apps_list,
				apps) {
			@SuppressLint("InflateParams")
			@NonNull
			@Override
			public View getView(int position, View convertView, @NonNull ViewGroup parent) {
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.apps_list, null);
				}
				ImageView appIcons = convertView.findViewById(R.id.item_app_icon);
				appIcons.setImageDrawable(apps.get(position).icon);
				TextView appLabels = convertView.findViewById(R.id.item_app_label);
				appLabels.setText(apps.get(position).label);
				TextView appNames = convertView.findViewById(R.id.item_app_name);
				appNames.setText(apps.get(position).name);
				return convertView;
			}
		};
		list.setAdapter(adapter);
	}



	//When the user clicks an item in the ListView
	//This code allows the click to be caught and launches
	//the application
	private void Click(){
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,
									long id) {
				Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
				AppsAdmin.this.startActivity(i);
			}
		});
	}
}

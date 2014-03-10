/** Main activity of the application. Allows users to go to Add Spot,
 *  Remove Spot, or Get Optimal Forecast features.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button show, choose, remove, notify /*seeData*/;
	int counter;
	Button add, sub;
	TextView display;
	String USER_DATA = "user_data.txt";
	String TIME_DATA = "time_data.txt";
	private PendingIntent pendingIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
	    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,0);
	    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
	    		System.currentTimeMillis() + 60000L,
	    	    6*60*60*1000L, pendingIntent);
		startService(new Intent(getBaseContext(), MyReceiver.class));
		choose = (Button) findViewById(R.id.chooseNew);
		remove = (Button) findViewById(R.id.removeSpot);
		show = (Button) findViewById(R.id.seeExisting);		
		//seeData = (Button) findViewById(R.id.seeData);
		display = (TextView) findViewById(R.id.tvDisplay);
		choose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.notify.surfnotification.GETREGIONACTIVITY");				
				startActivity(i);
				
			}
		});
		show.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.notify.surfnotification.BESTTIMEACTIVITY");
				startActivity(i);
			}
		});
		remove.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.notify.surfnotification.REMOVESPOTACTIVITY");
				startActivity(i);
			}
		});
		
		/*seeData.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.notify.surfnotification.SEEDATAACTIVITY");
				startActivity(i);
				
			}
		})*/;
	}
	
    public String[] readFile(String filename) {
		FileInputStream fis = null;
		String s = null;
		try {
			fis = openFileInput(filename);
			byte[] dataArray = new byte[fis.available()];
			if (dataArray.length == 0) {
				return new String[0];
			}
			while (fis.read(dataArray) != -1) {
				s = new String(dataArray);
			}			
			fis.close();
			return s.split("\\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[0];
		} catch (IOException e) {
			e.printStackTrace();
			return new String[0];
		}		
	}
    
}

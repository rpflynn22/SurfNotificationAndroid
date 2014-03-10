/** Used for testing and development purposes only. Not to actually
 *  be displayed to the user.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SeeDataActivity extends Activity {
	
	public final String FILENAME = "time_data.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_data);
		TextView tv = (TextView) findViewById(R.id.seeDataDisplay);
		HashMap<String, ArrayList<String>> map = readData(FILENAME);
		if (map == null) {
			tv.setText("You have not stored any spots!");
		} else {
			StringBuilder sb = new StringBuilder();
			for (String el : map.keySet()) {
				sb.append(el + ":\n");
				for (String li : map.get(el)) {
					sb.append("\t- ");
					sb.append(li);
					sb.append("\n");
				}
			}
			tv.setText(sb.toString());
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_data, menu);
		return true;
	}
	
    public HashMap<String, ArrayList<String>> readData(String filename) {
		FileInputStream fis = null;
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try {
			fis = openFileInput(filename);
			Scanner scan = new Scanner(fis);
			while (scan.hasNext()) {
				String match = scan.nextLine();
				String[] listDict = match.split(":");
				if (map.containsKey(listDict[0])) {
					ArrayList<String> l = map.get(listDict[0]);
					l.add(listDict[1]);
					map.put(listDict[0], l);
				} else {
					ArrayList<String> l = new ArrayList<String>();
					l.add(listDict[1]);
					map.put(listDict[0], l);
				}
			}
			scan.close();
			fis.close();
			return map;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
}

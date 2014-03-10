/** Gets the actual spot (or spots) that the user would like to add
 *  to his/her list of spots. Uses a multi choice alert dialog.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class GetSpotActivity extends Activity {
	TextView display;
	final String FILENAME = "user_data.txt";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_spot);
		display = (TextView) findViewById(R.id.tvDisplay);
		Bundle b = getIntent().getExtras();
		ArrayList<String> list = b.getStringArrayList("spotList");
		class spotChoices {
			public ArrayList<String> choices;
		}
		final spotChoices choices = new spotChoices();
		choices.choices = new ArrayList<String>();
		final String[] spots = list.toArray(new String[list.size()]);
		Arrays.sort(spots);
	    AlertDialog.Builder builder = new AlertDialog.Builder(GetSpotActivity.this);
	    builder.setTitle("Choose your spots")
	           .setMultiChoiceItems(spots, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       choices.choices.add(spots[which]);
	                   } else if (choices.choices.contains(spots[which])) {
	                       // Else, if the item is already in the array, remove it 
	                       choices.choices.remove(spots[which]);
	                       
	                   }
	               }
	           })
	           .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   //Toast.makeText(GetSpotActivity.this,"You Selected Area "+choices.choices.get(0)+" "+choices.choices.get(1),Toast.LENGTH_SHORT).show();
	            	   addToSpots(choices.choices);
	            	   finish();
	               }
	           })
	           .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   finish();
	               }
	           });
	    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public void addToSpots(ArrayList<String> spots) {
		String[] existingData = readFile();
		HashSet<String> beenPlaced = new HashSet<String>();
		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			for (String el : existingData) {
				fos.write((el + '\n').getBytes());
				beenPlaced.add(el);
			}
			for (String el : spots) {
				if (!beenPlaced.contains(el)) {
					fos.write((el + '\n').getBytes());
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	public String[] readFile() {
		//display = (TextView) findViewById(R.id.tvDisplay);
		FileInputStream fis = null;
		String s = null;
		try {
			fis = openFileInput(FILENAME);
			byte[] dataArray = new byte[fis.available()];
			if (dataArray.length == 0) {
				return new String[0];
			}
			while (fis.read(dataArray) != -1) {
				s = new String(dataArray);
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
					//return new String[0];
				}
				if (s == null) {
					return new String[0];
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		return s.split("\\n");
	}
	
}

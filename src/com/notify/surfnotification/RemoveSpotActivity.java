/** Allows the user to view his/her list of spots and choose one
 *  or more to delete. Modifies user_data.txt.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class RemoveSpotActivity extends Activity {
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_spot);
		tv = (TextView) findViewById(R.id.removeSpotDisplay);
		tv.setText("text");
		class spotChoices {
			public ArrayList<String> choices;
		}
		final String[] spots = readFile();
		final spotChoices choices = new spotChoices();
		choices.choices = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(RemoveSpotActivity.this);
	    builder.setTitle("Choose your spots to remove")
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
	           .setPositiveButton("remove", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   removeSpots(choices.choices);
	            	   finish();
	               }
	           })
	           .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   finish();
	               }
	           })
	           .setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
		});

	    
	    if (spots.length == 0) {
	    	tv.setText("You have no spots stored");
	    } else {
		    AlertDialog alert = builder.create();
		    alert.show();
	    }
	}
	
	public String[] readFile() {
		FileInputStream fis = null;
		String s = null;
		try {
			fis = openFileInput("user_data.txt");
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
	
	public void removeSpots(ArrayList<String> spots) {
		String[] existingData = readFile();
		FileOutputStream fos;
		try {
			fos = openFileOutput("user_data.txt", Context.MODE_PRIVATE);
			for (String el : existingData) {
				if (!spots.contains(el)) {
					fos.write((el + '\n').getBytes());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

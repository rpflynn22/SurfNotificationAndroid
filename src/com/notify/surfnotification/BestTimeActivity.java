/** Used to display the best time to go surfing within the range
 *  of the available forecast.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BestTimeActivity extends Activity {
	TextView tv, tv1;
	final String URL_FORMAT = new String("http://magicseaweed.com/api/bHR9ihtoB8bA57704gzfcQ9h0yr9rXi1/forecast/?spot_id=");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_best_time);
		tv = (TextView) findViewById(R.id.bestTimeDisplay);
		tv1 = (TextView) findViewById(R.id.detailsDisplay);
		tv.setText("Choose a spot");
		ImageView img = (ImageView) findViewById(R.id.bestTimeActivityMswImg);
		img.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v){
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.addCategory(Intent.CATEGORY_BROWSABLE);
		        intent.setData(Uri.parse("http://magicseaweed.com"));
		        startActivity(intent);
		    }
		});
		final String[] spots = readFile();
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(BestTimeActivity.this);
		builder.setTitle("Choose a spot:");
		builder.setSingleChoiceItems(spots, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String spot = spots[which];
						String spotID = lookupID(spot);
						tv.setText(spotID);
						String url = URL_FORMAT + spotID;
						try {
							HashMap<String, String> info = new SurfOptimum().execute(url, null, null).get();
							tv.setText(spot);
							tv1.setText(giveReport(spot, info));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		if (spots.length == 0) {
			tv.setText("You don't have any spots stored!");
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
			return new String[0];
		} catch (IOException e) {
			return new String[0];
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
				return new String[0];
			}
		    
		}
		return s.split("\\n");
	}
	
	public String lookupID(String spot) {
		HashMap<String, String> map = new HashMap<String, String>();
		Context context = BestTimeActivity.this;
		InputStream i = context.getResources().openRawResource(R.raw.msw_spot_id_dict);
		Scanner s = new Scanner(i);
		while (s.hasNext()) {
			String match = s.nextLine();
			String[] listDict = match.split(":");
			map.put(listDict[0], listDict[1]);
		}
		s.close();
		return map.get(spot);
	}
	
	public String giveReport(String spot, HashMap<String, String> info) {
		String description = String.format("\n\t- Day: %s\n\t- Time: %s\n\t"
				+ "- Rating: %s\n\t- Wave Height: %s\n", info.get("Day"), info.get("Time"),
				info.get("Rating"), info.get("MaxHeight"));
		return description;
	}
}

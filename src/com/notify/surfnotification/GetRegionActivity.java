/** Gets the region that contains the area that contains the spot
 *  the user would like to add. Uses a single choice alert dialog.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.notify.surfnotification.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class GetRegionActivity extends Activity {
	
	TextView display;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_region);
		display = (TextView) findViewById(R.id.tvDisplay);
		final HashMap<String, HashMap<String, LinkedList<String>>> hm = ParseDict.openFile("hi", GetRegionActivity.this, R.raw.n_america_spot_dict);
		Set<String> set = hm.keySet();
		final String[] regions = set.toArray(new String[set.size()]);
		Arrays.sort(regions);
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(GetRegionActivity.this);
		builder.setTitle("Select a region");
		builder.setSingleChoiceItems(regions, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();								
						Intent i = new Intent("com.notify.surfnotification.GETAREAACTIVITY");
						Bundle b = new Bundle();
						for (String el : hm.get(regions[which]).keySet()) {
							b.putSerializable(el, hm.get(regions[which]).get(el));
						}
						i.putExtras(b);
						startActivity(i);
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
	
	
}

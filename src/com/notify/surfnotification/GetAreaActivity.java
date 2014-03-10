/** Used to get the area (subset of a region) from the user which
 *  contains the spot the user intends to add. Uses an AlertDialog
 *  with a single choice list.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.notify.surfnotification.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GetAreaActivity extends Activity {
	TextView display;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_area);
		display = (TextView) findViewById(R.id.tvDisplay);
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Bundle b = getIntent().getExtras();
		for (String el : b.keySet()) {
			map.put(el, (ArrayList<String>) b.getSerializable(el));
		}
		final HashMap<String, ArrayList<String>> map1 = map;
		Set<String> set = map.keySet();
		final String[] areas = set.toArray(new String[set.size()]);
		Arrays.sort(areas);
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(GetAreaActivity.this);
		builder.setTitle("Select an area");
		builder.setSingleChoiceItems(areas, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent i = new Intent("com.notify.surfnotification.GETSPOTACTIVITY");
						Bundle b = new Bundle();
						b.putSerializable("spotList", map1.get(areas[which]));
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

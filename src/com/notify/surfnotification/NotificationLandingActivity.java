/** Activity which displays all the information which the user
 *  is being updated on in the notification. Landing page on
 *  click of notification.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationLandingActivity extends Activity {
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_landing);
		Bundle b = getIntent().getExtras();
		HashMap<String, ArrayList<HashMap<String, String>>> map =
				(HashMap<String, ArrayList<HashMap<String, String>>>) b.get("map");
		b.remove("map");
		ImageView img = (ImageView) findViewById(R.id.notificationLandingMswImg);
		img.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v){
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.addCategory(Intent.CATEGORY_BROWSABLE);
		        intent.setData(Uri.parse("http://magicseaweed.com"));
		        startActivity(intent);
		    }
		});
		Log.i("map", map.toString());
		TextView tv = (TextView) findViewById(R.id.notificationLandingDisplay);
		tv.setMovementMethod(new ScrollingMovementMethod());
		final String[] spots = map.keySet().toArray(new String[map.keySet().size()]);
		Arrays.sort(spots);
		StringBuilder sb = new StringBuilder();
		for (String spot : spots) {
			sb.append(spot);
			sb.append("\n\n");
			for (HashMap<String, String> info : map.get(spot)) {
				sb.append("\t- Time: ");
				sb.append(info.get("Time"));
				sb.append(' ');
				sb.append(info.get("Day"));
				sb.append("\n\t- Rating: ");
				sb.append(info.get("Rating"));
				sb.append("\n\t- Max Wave Height: ");
				sb.append(info.get("MaxHeight"));
				sb.append(" ft at ");
				sb.append(info.get("Period"));
				sb.append(" seconds\n\t- Wind: ");
				sb.append(info.get("WindSpeed"));
				sb.append(" mph, ");
				sb.append(info.get("WindDirection"));
				sb.append("\n\n");
			}
			
		}
		tv.setText(sb.toString());
	}

}

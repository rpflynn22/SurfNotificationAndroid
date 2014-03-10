/** Receiver for the repeating alarm.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service1 = new Intent(context, MyAlarmService.class);
	    context.startService(service1);
	}
}
/** Service that is executed periodically when alarm wakes it. 
 *  Checks the forecast for good surf days that the user has not
 *  yet been notified of, notifies the user of this time, and adds
 *  it to the list of times the user has been notified about. On clicking
 *  this notification, the user is taken to an activity that summarizes
 *  all of the updates. Additionally, if one of the times which the user 
 *  has been notified about already has passed, it is deleted from the list 
 *  of sent notifications for space saving. Also deletes from this list 
 *  spots that the user no longer has saved in his/her list of spots.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
                            
 
public class MyAlarmService extends Service {      
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    @Override
    public void onCreate() {
       super.onCreate();
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(getApplicationContext(), "Checking notifications", Toast.LENGTH_LONG).show();
        String[] spots = readFile("user_data.txt");
        if (spots.length == 0) {
        	return;
        }
        HashMap<String, String> spotID = new HashMap<String, String>();
        for (String spot : spots) {
    	   spotID.put(spot, lookupID(spot));
        }
        HashMap<String, ArrayList<HashMap<String, String>>> spotDes =
     		   new HashMap<String, ArrayList<HashMap<String, String>>>();
        HashMap<String, ArrayList<String>> existingTimes = readData("time_data.txt");
        if (existingTimes == null) {
        	existingTimes = new HashMap<String, ArrayList<String>>();
        }
        HashSet<String> spots1 = new HashSet<String>(Arrays.asList(spots));
        ArrayList<String> existingTimesKeys = new ArrayList<String>(existingTimes.keySet());
        for (int i = 0; i < existingTimesKeys.size(); i += 1) {
        	if (!spots1.contains(existingTimesKeys.get(i))) {
        		existingTimes.remove(existingTimesKeys.get(i));
        	}
        }
        for (String spot : spots) {
            try {
            	ArrayList<HashMap<String, String>> spotDList = new SolidSurf().execute(spotID.get(spot)).get();
            	if (spotDList != null) {
            		for (HashMap<String, String> m : spotDList) {
            			if (!existingTimes.containsKey(spot)
            					&& Long.parseLong(m.get("Timestamp"))*1000L > System.currentTimeMillis()) {
            				ArrayList<String> l = new ArrayList<String>();
            				l.add(m.get("Timestamp"));
            				existingTimes.put(spot, l);
            				if (spotDes.containsKey(spot)) {
            					ArrayList<HashMap<String, String>> spotDesList = spotDes.get(spot);
            					spotDesList.add(m);
            					spotDes.put(spot, spotDesList);
            				} else {
            					ArrayList<HashMap<String, String>> spotDesList = new ArrayList<HashMap<String, String>>();
            					spotDesList.add(m);
            					spotDes.put(spot, spotDesList);
            				}
            			}
            			else if (Long.parseLong(m.get("Timestamp"))*1000L > System.currentTimeMillis()
            					&& !existingTimes.get(spot).contains(m.get("Timestamp"))) {
            				ArrayList<String> l = existingTimes.get(spot);
            				l.add(m.get("Timestamp"));
            				existingTimes.put(spot, l);
            				if (spotDes.containsKey(spot)) {
            					ArrayList<HashMap<String, String>> spotDesList = spotDes.get(spot);
            					spotDesList.add(m);
            					spotDes.put(spot, spotDesList);
            				} else {
            					ArrayList<HashMap<String, String>> spotDesList = new ArrayList<HashMap<String, String>>();
            					spotDesList.add(m);
            					spotDes.put(spot, spotDesList);
            				}
            			}
            		}
            	}
    	    } catch (InterruptedException e) {
			    e.printStackTrace();
    	    } catch (ExecutionException e) {
			    e.printStackTrace();
    	    }
        }
        if (existingTimes.size() != 0) {
        	writeBack(existingTimes, "time_data.txt");
        }
        if (spotDes.size() > 0) {
        	notify(spotDes);
        }
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    public void notify(HashMap<String, ArrayList<HashMap<String, String>>> map) {
    	Intent i = new Intent(this, NotificationLandingActivity.class);
    	Bundle b = new Bundle();
    	b.putSerializable("map", map);
		i.putExtras(b);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    	StringBuilder sb = new StringBuilder();
    	HashMap<String, ArrayList<String>> p = readData("time_data.txt");
    	for (String el : map.keySet()) {
    		sb.append(el);
    		sb.append(": ");
    		for (HashMap<String, String> m : map.get(el)) {
    			sb.append(m.get("Time"));
    			sb.append(' ');
    			sb.append(m.get("Day"));
    			sb.append(' ');
    			sb.append(m.get("Rating"));
    			sb.append("/10; ");
    		}
    		sb.append(" | ");
    	}

    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.wave)
    	        .setContentTitle("SurfNotification")
    	        .setContentText(sb.toString())
    	        .setContentIntent(pIntent)
    	        .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
    	        .setLights(Color.RED, 3000, 3000)
    	        .setAutoCancel(true);


    	NotificationManager mNotificationManager =
    		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    	mNotificationManager.notify(0, mBuilder.build()); 
    }
    
    public void writeBack(HashMap<String, ArrayList<String>> map, String filename) {
    	for (String key : map.keySet()) {
    		ArrayList<String> list = map.get(key);
    		int i = 0;
    		while (i < list.size()) {
    			long timeStamp = Long.parseLong(list.get(i)) * 1000L;
    			if (timeStamp < System.currentTimeMillis()) {
    				list.remove(i);
    			} else {
    				i += 1;
    			}
    		}
    		map.put(key, list);
    	}
    	FileOutputStream fos;
		try {
			fos = openFileOutput("time_data.txt", Context.MODE_PRIVATE);
			for (String key : map.keySet()) {
				for (String el : map.get(key)) {
					fos.write((key + ":" + el + "\n").getBytes());
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    public HashMap<String, ArrayList<String>> readData(String filename) {
		FileInputStream fis = null;
		String s = null;
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
    
	public String lookupID(String spot) {
		HashMap<String, String> map = new HashMap<String, String>();
		Context context = this;
		InputStream i = context.getResources().openRawResource(R.raw.msw_spot_id_dict);
		Scanner s = new Scanner(i);
		while (s.hasNext()) {
			String match = s.nextLine();
			String[] listDict = match.split(":");
			map.put(listDict[0], listDict[1]);
		}
		return map.get(spot);
	} 
}

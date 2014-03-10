/** Returns a map that contains labels that map to the characteristics
 *  of the optimum time to go surfing in the range that the api forecast has
 *  access to.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class SurfOptimum extends AsyncTask <String, Void, HashMap<String, String>>{
	protected HashMap<String, String> doInBackground(String... urls) {
		HttpClient client = new DefaultHttpClient();
        String json = "";
        HashMap<String, String> significantItems = null;
        try {
            String line = "";
            HttpGet request = new HttpGet(urls[0]);
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while ((line = rd.readLine()) != null) {
                json += line + System.getProperty("line.separator");
            }
            HashSet<Integer> dni = new HashSet<Integer>();
    		dni.add(0); dni.add(1); dni.add(7);
    		JSONObject[] jArray = new JSONObject[40];
    		
    		json = json.substring(1, json.length() - 1);
    		String[] jsonStrings = json.split("\\},\\{");
    		for (int i = 0; i < jsonStrings.length - 1; i++) {
    			jsonStrings[i] += '}';
    			jsonStrings[i + 1] = '{' + jsonStrings[i + 1];
    			jArray[i] = new JSONObject(jsonStrings[i]);
    		}
    		jArray[jsonStrings.length - 1] = new JSONObject(jsonStrings[jsonStrings.length - 1]);
    		significantItems = new HashMap<String, String>();
    		int bestTime = -1;
    		int maxRating  = -1;
    		double maxHeight = -1;
    		for (int i = 0; i < jArray.length; i += 1) {
    			int currRating = jArray[i].getInt("fadedRating") + jArray[i].getInt("solidRating") * 2;
    			double currHeight = ((JSONObject) jArray[i].get("swell")).getDouble("absMaxBreakingHeight");
    			if (currRating >= maxRating && !dni.contains(i % 8)) {
    				if (currRating > maxRating || (currRating == maxRating
    						&& currHeight > maxHeight)) {
    					maxRating = currRating;
    					bestTime = i;
    					maxHeight = currHeight;
    				}
    			}
    		}
    		String[] time = makeDay(bestTime);
    		significantItems.put("Day", time[0]);
    		significantItems.put("Time", time[1]);
    		significantItems.put("Rating", Integer.toString(maxRating));
    		significantItems.put("MaxHeight", ((JSONObject) jArray[bestTime].get("swell")).get("absMaxBreakingHeight").toString());
    		significantItems.put("Period", ((JSONObject) ((JSONObject) ((JSONObject) jArray[bestTime].get("swell")).get("components")).get("combined")).get("period").toString());
    		significantItems.put("WindSpeed", ((JSONObject) jArray[bestTime].get("wind")).get("speed").toString());
    		significantItems.put("WindDirection", ((JSONObject) jArray[bestTime].get("wind")).get("direction").toString());
    		return significantItems;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return significantItems;
	}
	
	protected void onProgressUpdate(Void... progress) {
		
	}
	
	protected void onPostExecute(String result) {
		
	}
	
	public static String[] makeDay(int rep) {
		String[] days = {"Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		String[] times = {"12 a.m.", "3 a.m.", "6 a.m.", "9 a.m.", "12 p.m.", "3 p.m.", "6 p.m.", "9 p.m."};
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		String surfDay = days[(day + (int) (rep / 8)) % 7];
		String surfTime = times[rep % 8];
		String[] time = {surfDay, surfTime};
		return time;
	}
}

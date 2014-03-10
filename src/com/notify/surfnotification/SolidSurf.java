/** As opposed to SurfOptimum.java, returns a list of days/times
 *  when the best rating for the day is above MIN_RATING.
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

public class SolidSurf extends AsyncTask <String, Void, ArrayList<HashMap<String, String>>>{
	final int MIN_RATING = 7;
	protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
		HttpClient client = new DefaultHttpClient();
        String json = "";
        try {
            String line = "";
            String url1 = "http://magicseaweed.com/api/bHR9ihtoB8bA57704gzfcQ9h0yr9rXi1/forecast/?spot_id=";
            HttpGet request = new HttpGet(url1 + urls[0]);
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
    		ArrayList<ArrayList<JSONObject>> days = new ArrayList<ArrayList<JSONObject>>();
    		ArrayList<JSONObject> day = null;
    		ArrayList<JSONObject> dayOptimums = new ArrayList<JSONObject>();
    		ArrayList<HashMap<String, String>> descriptions = new ArrayList<HashMap<String, String>>();
    		for (int i = 0; i < jArray.length; i += 1) {
    			if (i % 8 == 0) {
    				day = new ArrayList<JSONObject>();
    			}
    			day.add(jArray[i]);
    			if (i % 8 == 7) {
    				days.add(day);
    			}
    		}
    		for (int j = 0; j < days.size(); j += 1) {
    			int bestTime = -1;
        		int maxRating  = -1;
        		double maxHeight = -1;
        		for (int i = 0; i < days.get(j).size(); i += 1) {
        			int currRating = days.get(j).get(i).getInt("fadedRating") + days.get(j).get(i).getInt("solidRating") * 2;
        			double currHeight = ((JSONObject) days.get(j).get(i).get("swell")).getDouble("absMaxBreakingHeight");
        			if (currRating >= maxRating && !dni.contains(i)) {
        				if (currRating > maxRating || (currRating == maxRating
        						&& currHeight > maxHeight)) {
        					maxRating = currRating;
        					bestTime = i;
        					maxHeight = currHeight;
        				}
        			}
        		}
        		if (maxRating >= MIN_RATING) {
        			dayOptimums.add(days.get(j).get(bestTime));
        			HashMap<String, String> dayMap = new HashMap<String, String>();
        			int overallIndex = (8 * j) + bestTime;
        			String[] time = makeDay(overallIndex);
        			dayMap.put("Day", time[0]);
        			dayMap.put("Time", time[1]);
        			dayMap.put("Rating", Integer.toString(maxRating));
        			dayMap.put("MaxHeight", ((JSONObject) days.get(j).get(bestTime).get("swell")).get("absMaxBreakingHeight").toString());
        			dayMap.put("Period", ((JSONObject) ((JSONObject) ((JSONObject) days.get(j).get(bestTime)
        					.get("swell")).get("components")).get("combined")).get("period").toString());
        			dayMap.put("WindSpeed", ((JSONObject) days.get(j).get(bestTime).get("wind")).get("speed").toString());
        			dayMap.put("WindDirection", ((JSONObject) days.get(j).get(bestTime).get("wind")).get("compassDirection").toString());
        			dayMap.put("Timestamp", days.get(j).get(bestTime).get("timestamp").toString());
        			descriptions.add(dayMap);
        		}
    		}
    		if (dayOptimums.size() == 0) {
    			return null;
    		} else {
    			return descriptions;
    		}
    		
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
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
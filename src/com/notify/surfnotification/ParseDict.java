/** Returns a dictionary that maps a hierarchy of regions, areas, and spots.
 *  Based on textfile in raw resource folder. 
 *  @author Ryan P Flynn
 */

package com.notify.surfnotification;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.MatchResult;

import android.content.Context;

public class ParseDict {
	
	public static HashMap<String, HashMap<String, LinkedList<String>>> openFile(String filename, Context context, int refInt) {
		try {
			InputStream i = context.getResources().openRawResource(refInt);
			Scanner s = new Scanner(i);
			return parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String, HashMap<String, LinkedList<String>>> parse (Scanner s) {
		HashMap<String, HashMap<String, LinkedList<String>>> spotMap = new
				HashMap<String, HashMap<String, LinkedList<String>>>();
		String pattern = "'([^\\}\\]]{0,50})': \\{([^\\}]*)\\}";
		String innerPattern = "'([^:\\]\\[']{0,50})': \\[([^\\]]*)\\]";
		String listPattern = "'([^,]{0,50})'";
		MatchResult m, m1, m2;
		while (s.hasNext()) {
			HashMap<String, LinkedList<String>> areaToSpot = new HashMap<String, LinkedList<String>>();
			String item = s.findInLine(pattern);	//Like California, South
			if (item == null) {
				break;
			}
			m = s.match();
			String bigRegion = m.group(1);
			String innerDict = m.group(2);
			Scanner s1 = new Scanner(innerDict);
			while (s1.hasNext()) {
				String item1 = s1.findInLine(innerPattern);
				if (item1 == null) {
					break;
				}
				m1 = s1.match();
				String area = m1.group(1);		//Like Orange County
				String spotList = m1.group(2);
				Scanner s2 = new Scanner(spotList);
				LinkedList<String> spotLink = new LinkedList<String>();
				while (s2.hasNext()) {
					String item2 = s2.findInLine(listPattern);		
					if (item2 == null) {
						break;
					}
					m2 = s2.match();
					String spot = m2.group(1);		//Like Huntington
					spotLink.add(spot);
				}
				s2.close();
				areaToSpot.put(area, spotLink);
			}
			s1.close();
			spotMap.put(bigRegion, areaToSpot);			
		}
		s.close();
		return spotMap;
	}
}

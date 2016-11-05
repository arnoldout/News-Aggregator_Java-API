package main.java.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLDoc {
	public String url;

	public List<String> parseText(Set<String> blackList) {
		Map<String, Integer> words = new HashMap<String, Integer>();
		Document doc;
		AtomicInteger count = new AtomicInteger(0);
		
		try {
			doc = Jsoup.connect(url).timeout(10*1000).get();
			String str = Jsoup.parse(doc.toString()).text();
			String[] arr = str.split(" ");
			for (String ss : arr) {
				count.getAndIncrement();
				ss = ss.toLowerCase();
				ss = ss.replaceAll("[^a-zA-Z]", "");
				ss = ss.trim();
				if (!(blackList.contains(ss))&&!ss.equals("")) {
					if (words.containsKey(ss)) {
						words.put(ss, words.get(ss) + 1);
					} else {
						words.put(ss, 1);
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		@SuppressWarnings("unchecked")
		Map.Entry<String,Integer>[] entries = words.entrySet().toArray(new Map.Entry[0]);
        Arrays.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<String> topEntries = new ArrayList<String>();
        int i = 0;
        for(Map.Entry<String,Integer> entry:entries){
        	//need to refine searches down	
        	if(i>=4)
        	{
        		break;
        	}
        	else{
	            topEntries.add(entry.getKey());
	            i++;
        	}
        }
        return topEntries;
	}
}

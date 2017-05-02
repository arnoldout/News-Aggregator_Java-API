package main.java.types;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/*
 * HTMLDoc is a web scraper that takes in a url, and scrapes the page.
 * It generates the most frequently occuring words on the page, and stores them
 */
public class HTMLDoc {
	public String url;

	
	public String stemTerm(String text){
        StringBuffer result = new StringBuffer();
        if (text!=null && text.trim().length()>0){
            StringReader tReader = new StringReader(text);
            Analyzer analyzer = new StandardAnalyzer();
            TokenStream tStream = analyzer.tokenStream("contents", tReader);
            CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);

            try {
                while (tStream.incrementToken()){
                    result.append(term);
                    result.append(" ");
                }
            } catch (IOException ioe){
                System.out.println("Error: "+ioe.getMessage());
            }
        }

        // If, for some reason, the stemming did not happen, return the original text
        if (result.length()==0)
            result.append(text);
        return result.toString().trim();
    }

	// parse text from url
	public List<String> parseText(Set<String> blackList) {
		Map<String, Integer> words = new HashMap<String, Integer>();
		Document doc;
		AtomicInteger count = new AtomicInteger(0);

		try {
			// strip text from website, unless timeout hit
			doc = Jsoup.connect(url).timeout(10 * 1000).get();
			String str = Jsoup.parse(doc.toString()).text();
			//str = "Happiest Happy happy happier";
//			str = stemTerm(str);
//			str = stemTerm("happy");
//			str = stemTerm("happier");
//			str = stemTerm("happiest");
//			str = stemTerm("strengthen");
//			str = stemTerm("strength");
//			str = stemTerm("hardest");
//			str = stemTerm("hardy");
//			str = stemTerm("hard");
			String[] arr = str.split(" ");

			// loop through array of all words in html
			for (String ss : arr) {
				count.getAndIncrement();

				// all words to lowercase
				ss = ss.toLowerCase();
				// strip html tags
				ss = ss.replaceAll("[^a-zA-Z]", "");
				// remove white space
				ss = ss.trim();
				// if word is unique and not on blacklist of frequently occuring
				// words, add to list
				// if word already in list, increment counter of word occurances
				if (!(blackList.contains(ss)) && !ss.equals("")) {
					if (words.containsKey(ss)) {
						words.put(ss, words.get(ss) + 1);
					} else {
						words.put(ss, 1);
					}
				}
			}

		} catch (IOException e) {
			System.out.println("Failed to Parse :"+this.url);
			return null;
		}
		// convert array to map
		// order array
		@SuppressWarnings("unchecked")
		Map.Entry<String, Integer>[] entries = words.entrySet().toArray(new Map.Entry[0]);
		Arrays.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		List<String> topEntries = new ArrayList<String>();
		int i = 0;
		for (Map.Entry<String, Integer> entry : entries) {
			// need to refine searches down
			if (i >= 4) {
				break;
			} else {
				topEntries.add(entry.getKey());
				i++;
			}
		}
		return topEntries;
	}
}

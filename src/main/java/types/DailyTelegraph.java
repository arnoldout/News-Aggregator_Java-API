package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class DailyTelegraph extends XMLDoc {

	List<String> urls = new ArrayList<String>(Arrays.asList("http://www.telegraph.co.uk/travel/rss.xml",
			"http://www.telegraph.co.uk/news/rss.xml", "http://www.telegraph.co.uk/sport/rss.xml"));

	@Override
	public void parseXml() {
		for (String url : urls) {
			try {
				URL feedSource = new URL(url);

				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));

				List<SyndEntry> items = feed.getEntries();
				for (SyndEntry entry : items) {
					NeverNullString uri = new NeverNullString(entry.getLink());
					NeverNullString title = new NeverNullString(entry.getTitle());
					NeverNullString desc = new NeverNullString("No Description Provided");
					NeverNullString imgUri;
					if(!entry.getEnclosures().isEmpty()){
						imgUri = new NeverNullString(entry.getEnclosures().get(0).getUrl(),"http://www.telegraph.co.uk/template/ver1-0/i/telegraphFacebook.jpg");
					}
					else{
						imgUri = new NeverNullString("http://www.telegraph.co.uk/template/ver1-0/i/telegraphFacebook.jpg");
					}
					Story item = new Story(uri, title, desc, imgUri);
					super.add(item);
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FeedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

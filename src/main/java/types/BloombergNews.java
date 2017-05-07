package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class BloombergNews extends XMLDoc {
	// link to bbc rss feed
	public final String url = "https://www.bloomberg.com/politics/feeds/site.xml";

	// parse rss feed for stories
	@Override
	public void parseXml() {
		try {
			URL feedSource = new URL(url);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));

			List<SyndEntry> items = feed.getEntries();
			for (SyndEntry entry : items) {
				Story item = new Story(entry.getUri());
				try{
				item.setDescription(entry.getDescription().getValue());
				}
				catch(NullPointerException e)
				{
					item.setDescription("No Description Provided");
				}
				item.setTitle(entry.getTitle());
				try{
					item.setImgUri(entry.getForeignMarkup().get(0).getAttribute("url").getValue());
				}
				catch(IndexOutOfBoundsException e)
				{
					//no image found
					item.setImgUri("https://media.glassdoor.com/sqll/3096/bloomberg-l-p-squarelogo-1485356219895.png");
				}
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
